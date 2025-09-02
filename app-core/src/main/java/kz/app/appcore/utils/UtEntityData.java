package kz.app.appcore.utils;

import kz.app.appcore.model.DbRec;
import kz.app.appdbtools.repository.Db;

import java.util.List;
import java.util.Map;

public class UtEntityData {
    Db db;
    String tableName;

    public UtEntityData(Db db, String tableName) {
        this.db = db;
        this.tableName = tableName;
    }

    public long insertEntity(DbRec rec) throws Exception {
        if (!UtCnv.toString(rec.get("tableName")).isEmpty())
            tableName = UtCnv.toString(rec.get("tableName"));
        //
        rec.putIfAbsent("accessLevel", 1L);
        rec.putIfAbsent("dbeg", "1800-01-01");
        rec.putIfAbsent("dend", "3333-12-31");

        String cod = rec.getString("cod");
        //
        long id1 = 0;
        if (tableName.equalsIgnoreCase("Obj")) {
            id1 = rec.getLong("cls");
            if (id1==0L)
                throw new XError("NotFoundCls");
        }
        if (tableName.equalsIgnoreCase("RelObj")) {
            id1 = rec.getLong("relcls");
            if (id1==0L)
                throw new XError("NotFoundRelCls");
        }
        //
        checkCod(cod);
        long id = 0; //db.getNextId(tableName);
        rec.put("id", id);
        //
        //DomainService domainSvc = mdb.getModel().bean(DomainService.class);
        //Domain dm = domainSvc.getDomain(tableName);
        //if (dm.findField("ord") != null) {
            rec.put("ord", id);
        //}
        //



        return 0;
    }

    protected void checkCod(String cod) throws Exception {
        if (cod.startsWith(EntityConstData.genCodPref)) {
            throw new XError("Код {0} не может начинаться с символа «_»", cod);
        }
        if (!EntityConstData.isCodValid(cod)) {
            throw new XError("Допустимыми символами для кода {0} являются: " +
                    "цифры, буквы на английском, «_», « - », « / », « . »", cod);
        }
        checkCodUnique(cod);
    }


    /**
     * Проверка уникальности кода сущности
     */
    protected void checkCodUnique(String cod) throws Exception {
        List<DbRec> ds = db.loadSql("""                        
                    select entitytype, entityid from syscod
                    where UPPER(cod)=UPPER(:cod)
                """, Map.of("cod", cod));

        if (!ds.isEmpty()) {
            long entityType = ds.getFirst().getLong("entitytype");
            long entityId = ds.getFirst().getLong("entityid");

            EntityConstData.EntityInfo entityInfo = EntityConstData.getEntityInfo(entityType);
            String tableName = entityInfo.getTableName();
            String sql = "select name from " + tableName + " where id=:id";
            List<DbRec> st = db.loadSql(sql, Map.of("id", entityId));
            String inst = st.getFirst().getString("name");
            throw new XError("Введенный код [{3}] является кодом экземпляра [{1}] сущности [{0}]", entityInfo.getText(), inst, cod);
        }
    }


}
