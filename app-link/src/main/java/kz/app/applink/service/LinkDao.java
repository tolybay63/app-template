package kz.app.applink.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LinkDao {
    private final Db dbLink;


    public LinkDao(@Qualifier("dbLink") Db dbLink) {
        this.dbLink = dbLink;
    }

    public List<DbRec> getCls(String codTyp) throws Exception {
        DbRec pms = getIdFromCodOfEntity("Typ", codTyp, "");
        long id = pms.getLong(codTyp);
        List<DbRec> st = dbLink.loadSql("""
            select c.id, v.name  from Cls c, ClsVer v
            where c.id=v.ownerVer and v.lastVer=1 and c.typ=:id
        """, Map.of("id", id));
        if (st.isEmpty()) {
            throw new XError("NotFoundCod@{0}", codTyp);
        }
        return st;
    }

    public String getIdsCls(String codTyp) throws Exception {
        DbRec pms = getIdFromCodOfEntity("Typ", codTyp, "");
        long id = pms.getLong(codTyp);
        List<DbRec> st = dbLink.loadSql("select id from Cls where typ=:id", Map.of("id", id));
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

    public DbRec getIdFromCodOfEntity(String entity, String cod, String prefixcod) throws Exception {
        String sql = "select id, cod from " + entity + " where cod like :cod";
        if (!prefixcod.isEmpty()) {
            sql = "select id, cod from " + entity + " where cod like :prefixcod";
        }
        List<DbRec> st = dbLink.loadSql(sql, Map.of("cod", cod, "prefixcod", prefixcod));
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
        return dbLink.loadSql("""
                    select pv.id, pv.factorVal, f.name
                    from PropVal pv
                        left join Factor f on pv.factorVal=f.id
                    where pv.id in
                """ + idsPV, null);
    }

    public Set<Long> getIdsPV(int isObj, long clsORrel, String codProp) throws Exception {
        String whe = "";
        if (!codProp.isEmpty()) {
            DbRec map = getIdFromCodOfEntity("Prop", codProp, "");
            whe = " and prop=" + map.getLong(codProp);
        }

        String fld = "cls";
        if (isObj == 0)
            fld = "relcls";
        List<DbRec> st = dbLink.loadSql("select id as pv from PropVal where " + fld + "=:clsORrel" + whe,
                Map.of("clsORrel", clsORrel));
        Set<Long> set = UtDb.uniqueValues(st, "pv");
        if (set.isEmpty()) set.add(0L);
        return set;
    }


}
