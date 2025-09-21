package kz.app.appdata.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtCnv;
import kz.app.appcore.utils.UtString;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UtEntityData {
    Db db;
    String tableName;

    public UtEntityData(Db db, String tableName) {
        this.db = db;
        this.tableName = tableName;
    }

    public long getNextId(String tableName) throws Exception {
        String sql = "SELECT nextval('g_"+tableName+"') as id";
        List<DbRec> lst = db.loadSql(sql, null);
        return lst.getFirst().getLong("id");
    }

    private boolean existsField(String tableName, String fieldName) throws Exception {
        return db.loadSql("""
            SELECT exists (
                SELECT 1
                FROM information_schema.columns
                WHERE table_schema = 'public'
                AND table_name = :tableName
                AND column_name = :fieldName
            )
        """, Map.of("tableName", tableName.toLowerCase(), "fieldName", fieldName.toLowerCase())).getFirst().getBoolean("exists");
    }

    public DbRec setDomain(String tableName, DbRec params) throws Exception {
        DbRec rec = new DbRec();
        List<DbRec> st = db.loadSql("""
            SELECT column_name
            FROM information_schema.columns
            WHERE table_schema = 'public'
            AND table_name=:tb
        """, Map.of("tb", tableName.toLowerCase()));
        List<String> lst = new ArrayList<>();
        for (DbRec r : st) {
            lst.add(r.getString("column_name"));
        }
        for (String key : params.keySet()) {
            if (lst.contains(key.toLowerCase())) {
                rec.put(key, params.get(key));
            }
        }
        return rec;
    }

    public long insertEntity(DbRec params) throws Exception {
        if (!params.getString("tableName").isEmpty())
            tableName = params.getString("tableName");

        DbRec rec = setDomain(tableName, params);
        rec.putIfAbsent("accessLevel", 1L);
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
        long id = getNextId(tableName);
        rec.put("id", id);
        //
        if (existsField(tableName, "ord")) {
            rec.put("ord", id);
        }
        //
        long ent = EntityConstData.getNumConst(tableName);
        if (cod.isEmpty()) {
            cod = EntityConstData.generateCod(ent, id1, id);
            rec.put("cod", cod);
        }
        rec.put("timeStamp", LocalDateTime.now());
        //
        db.insertRec(tableName, rec);
        // добавляем код
        db.insertRec("SysCod", Map.of("id", getNextId("SysCod"),"cod", cod, "entityType", ent, "entityId", id));
        //
        if (EntityConstData.getEntityInfo(ent).getHasVer()) {
            DbRec rV = setDomain(tableName+"Ver", params);

            rV.putIfAbsent("dbeg", LocalDate.parse("1800-01-01", DateTimeFormatter.ISO_DATE));
            rV.putIfAbsent("dend", LocalDate.parse("3333-12-31", DateTimeFormatter.ISO_DATE));

            if (!params.getString("cmt").isEmpty())
                rV.putIfAbsent("cmtVer", params.getString("cmt"));

            long idVer = getNextId(tableName + "Ver");
            rV.put("id", idVer);
            rV.put("ownerVer", id);
            rV.put("lastVer", 1);
            if (tableName.equalsIgnoreCase("obj")) {
                long parent = params.getLong("parent");
                if (parent > 0)
                    rV.put("objParent", parent);
            }
            db.insertRec(tableName + "Ver", rV);
        }
        //
        return id;
    }

    public void deleteEntity(long id) throws Exception {

        String sign = tableName;
        long ent = EntityConstData.getNumConst(sign);
        if (tableName.equalsIgnoreCase("RelObj")) {
            db.execSql("""
                    delete from RelObjMember
                    where relobj=:id
                """, Map.of("id", id));
        }
        db.execSql("""
                    delete from SysCod
                    where entityType=:entityType and entityId=:entityId
                """, Map.of("entityType", ent, "entityId", id));

        if (EntityConstData.getEntityInfo(ent).getHasVer()) {
            db.execSql("delete from " + tableName + "Ver where ownerVer=:id", Map.of("id", id));
        }
        //
        db.deleteRec(tableName, id);
    }

    public void updateEntity(DbRec params) throws Exception {
        if (!params.getString("tableName").isEmpty())
            tableName = params.getString("tableName");
        DbRec rec = setDomain(tableName, params);
        rec.putIfAbsent("accessLevel", 1L);
        //
        long id = rec.getLong("id");
        if (existsField(tableName, "ord")) {
            rec.put("ord", id);
        }
        DbRec oldRec = db.loadRec(tableName, id);
        // Checking cod
        String cod = rec.getString("cod");
        String oldCod = oldRec.getString("cod");
        //
        if (!UtString.empty(cod) && !cod.equalsIgnoreCase(oldCod)) {
            checkCod(cod);
        }
        //
        long id1 = 0;
        if (tableName.equalsIgnoreCase("Obj"))
            id1 = UtCnv.toLong(rec.get("cls"));
        if (tableName.equalsIgnoreCase("RelObj"))
            id1 = UtCnv.toLong(rec.get("relcls"));

        long ent = EntityConstData.getNumConst(tableName);
        // генерим код, если не указан
        if (cod.isEmpty()) {
            cod = EntityConstData.generateCod(ent, id1, id);
            rec.put("cod", cod);
        }
        // изменяем код
        if (!cod.equalsIgnoreCase(oldCod)) {
            db.execSql("""
                        update SysCod set cod=:cod
                        where entityType=:entityType and entityId=:entityId
                    """, Map.of("cod", cod, "entityType", ent, "entityId", id));
        }

        if (existsField(tableName, "timeStamp"))
            rec.put("timeStamp", LocalDateTime.now());
        db.updateRec(tableName, rec);
        //
        if (EntityConstData.getEntityInfo(ent).getHasVer()) {
            long verId = db.loadSql("select v.id from " + tableName + " t," + tableName + "Ver v " +
                            "where t.id=v.ownerVer and v.lastVer=1 and t.id=:id",
                    Map.of("id", id)).getFirst().getLong("id");

            //st = mdb.createStore(tableName + "Ver");
            DbRec rV = setDomain(tableName+"Ver", params);
            rV.put("id", verId);
            rV.put("ownerVer", id);
            rV.put("lastVer", 1);
            if (rV.getString("cmtVer").isEmpty() && !rec.getString("cmt").isEmpty())
                rV.put("cmtVer", rec.getString("cmt"));
            if (tableName.equalsIgnoreCase("obj")) {
                long parent = UtCnv.toLong(rec.get("parent"));
                if (parent > 0)
                    rV.put("objParent", parent);
            }
            db.updateRec(tableName + "Ver", rV);
        }
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
