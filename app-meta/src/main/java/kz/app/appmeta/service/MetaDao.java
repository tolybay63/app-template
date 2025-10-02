package kz.app.appmeta.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MetaDao {

    private final Db dbMeta;

    public MetaDao(@Qualifier("dbMeta") Db dbMeta) {
        this.dbMeta = dbMeta;
    }

    public List<DbRec> loadDict(String dictName, long accessLevel) throws Exception {
        return dbMeta.loadSql("select id, text from "+dictName + " where id <= "+accessLevel, null);
    }



    public DbRec getIdFromCodOfEntity(String entity, String cod, String prefixcod) throws Exception {
        String sql = "select id, cod from " + entity + " where cod like :cod";
        if (!prefixcod.isEmpty()) {
            sql = "select id, cod from " + entity + " where cod like :prefixcod";
        }
        List<DbRec> st = dbMeta.loadSql(sql, Map.of("cod", cod, "prefixcod", prefixcod));
        if (st.isEmpty()) {
            String cd = cod.isEmpty() ? prefixcod : cod;
            throw new XError("NotFoundCod@{0}", cd);
        }
        DbRec map = new DbRec();
        for (DbRec r : st) {
            map.put(r.getString("cod"), r.getLong("id"));
        }
        return map;
    }

    public List<DbRec> getFactorValsInfo(String idsPV) throws Exception {
        return dbMeta.loadSql("""
            select pv.id, pv.factorVal, f.name
            from PropVal pv
                left join Factor f on pv.factorVal=f.id
            where pv.id in
        """ + idsPV,  null);
    }

    public Map<Long, Long> mapEntityIdFromPV(String entity, boolean keyIsPropVal) throws Exception {
        Map<Long, Long> res = new HashMap<>();
        List<DbRec> st = dbMeta.loadSql("select id,"+entity+" from PropVal where "+entity+ " is not null", null);
        for (DbRec r : st) {
            if (keyIsPropVal)
                res.put(r.getLong("id"), r.getLong(entity));
            else
                res.put(r.getLong(entity), r.getLong("id"));
        }
        return res;
    }


    public String getIdsCls(String codTyp) throws Exception {
        DbRec pms = getIdFromCodOfEntity("Typ", codTyp, "");
        long id = pms.getLong(codTyp);
        List<DbRec> st = dbMeta.loadSql("select id from Cls where typ=:id", Map.of("id", id));
        if (st.isEmpty()) {
            throw new XError("NotFoundCod@{0}", codTyp);
        }
        Set<Long> ids = st.stream()
                .map(map -> (Long) map.get("id"))
                .collect(Collectors.toSet());
        String whe = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return "(" + whe + ")";
    }

    public List<DbRec> getCls(String codTyp) throws Exception {
        DbRec pms = getIdFromCodOfEntity("Typ", codTyp, "");
        long id = pms.getLong(codTyp);
        List<DbRec> st = dbMeta.loadSql("""
            select c.id, v.name  from Cls c, ClsVer v
            where c.id=v.ownerVer and v.lastVer=1 and c.typ=:id
        """, Map.of("id", id));
        if (st.isEmpty()) {
            throw new XError("NotFoundCod@{0}", codTyp);
        }
        return st;
    }


    public List<DbRec> getPropInfo(String codProp) throws Exception {
        List<DbRec> res = dbMeta.loadSql("""
                    select p.id, p.cod, p.propType, a.attribValType, p.isUniq, p.isdependvalueonperiod as dependPeriod,
                        p.statusFactor, p.providerTyp, m.kfrombase as koef, p.digit
                    from Prop p
                        left join Attrib a on a.id=p.attrib
                        left join Measure m on m.id=p.measure
                    where p.cod like :c
                """, Map.of("c", codProp));

        if (res.isEmpty()) {
            throw new XError("NotFoundPropCod@" + codProp);
        }
        return res;
    }

    public long getDefaultStatus(long prop) throws Exception {
        List<DbRec> st = dbMeta.loadSql("""
                    select factorVal from PropStatus where prop=:prop and isDefault is true
                """, Map.of("prop", prop));
        if (st.isEmpty())
            throw new XError("Not found default status");

        return st.getFirst().getLong("factorVal");
    }

    public List<DbRec> getIdsPV(int isObj, long clsORrel) throws Exception {
        String fld = "cls";
        if (isObj == 0)
            fld = "relcls";
        return dbMeta.loadSql("select id as pv from PropVal where " +fld + "=:clsORrel",
                Map.of("clsORrel", clsORrel));
    }

    public long getLinkCls(long cls,long typ) throws Exception {
        List<DbRec> st = dbMeta.loadSql("""
                with fv as (
                    select cls,
                    string_agg (cast(factorval as varchar(2000)), ',' order by factorval) as fvlist
                    from clsfactorval
                    where cls=:cls
                    group by cls
                )
                select * from (
                    select c.cls,
                    string_agg (cast(c.factorval as varchar(1000)), ',' order by factorval) as fvlist
                    from clsfactorval c, factor f
                    where c.factorval =f.id and c.cls in (
                        select id from Cls where typ=:typ
                    )
                    group by c.cls
                ) t where t.fvlist in (select fv.fvlist from fv)
                """, Map.of("cls", cls, "typ", typ));
        if (st.isEmpty())
            throw new XError("Not found cls");

        return st.getFirst().getLong("cls");
    }

}
