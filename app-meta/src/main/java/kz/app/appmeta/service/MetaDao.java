package kz.app.appmeta.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MetaDao {

    private final Db dbMeta;

    public MetaDao(@Qualifier("dbMeta") Db dbMeta) {
        this.dbMeta = dbMeta;
    }

    public DbRec getIdFromCodOfEntity(String entity, String cod, String prefixcod) throws Exception {
        String sql = "select id, cod from " + entity + " where cod like :cod";
        if (!prefixcod.isEmpty()) {
            //prefixcod = prefixcod + "%";
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


}
