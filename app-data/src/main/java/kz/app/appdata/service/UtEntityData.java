package kz.app.appdata.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtCnv;
import kz.app.appcore.utils.UtPeriod;
import kz.app.appcore.utils.UtString;
import kz.app.appcore.utils.XError;
import kz.app.appcore.utils.consts.FD_AttribValType_consts;
import kz.app.appcore.utils.consts.FD_InputType_consts;
import kz.app.appcore.utils.consts.FD_PeriodType_consts;
import kz.app.appcore.utils.consts.FD_PropType_consts;
import kz.app.appdbtools.repository.Db;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public void deleteOwnerWithProperties(long obj) throws Exception {
        db.execSql("""
            delete from DataPropVal
            where dataProp in (select id from DataProp where isObj=1 and objorrelobj=:id);
            delete from DataProp where id in (
                select id from dataprop
                except
                select dataProp as id from DataPropVal
            );
        """, Map.of("id", obj));
        //
        deleteEntity(obj);
    }

    public void saveObjWithProps(String mode, DbRec params, Map<String, DbRec> mapProp) throws Exception {
        if (mode.equalsIgnoreCase("ins")) {
            for (String keyProp: mapProp.keySet()) {
                if (mapProp.get(keyProp).getLong("propType")== FD_PropType_consts.attr) {
                    if (!params.getString(keyProp.split("_")[1]).isEmpty() ) {
                        fillProperties(keyProp, params, mapProp.get(keyProp));
                    }
                } if (mapProp.get(keyProp).getLong("propType")== FD_PropType_consts.meter) {
                    if (!params.getString(keyProp.split("_")[1]).isEmpty() ) {
                        fillProperties(keyProp, params, mapProp.get(keyProp));
                    }
                }  else if (mapProp.get(keyProp).getLong("propType")== FD_PropType_consts.factor) {
                    if (params.getLong("fv" + keyProp.split("_")[1]) > 0 ) {
                        fillProperties(keyProp, params, mapProp.get(keyProp));
                    }
                }  else if (mapProp.get(keyProp).getLong("propType")== FD_PropType_consts.typ) {
                    if (params.getLong("obj" + keyProp.split("_")[1]) > 0 ) {
                        fillProperties(keyProp, params, mapProp.get(keyProp));
                    }
                }  else if (mapProp.get(keyProp).getLong("propType")== FD_PropType_consts.reltyp) {
                    if (params.getLong("relobj" + keyProp.split("_")[1]) > 0 ) {
                        fillProperties(keyProp, params, mapProp.get(keyProp));
                    }
                }  else if (mapProp.get(keyProp).getLong("propType")== FD_PropType_consts.measure) {
                    if (params.getLong("mea" + keyProp.split("_")[1]) > 0 ) {
                        fillProperties(keyProp, params, mapProp.get(keyProp));
                    }
                }
            }
        } else if (mode.equalsIgnoreCase("upd")) {
            for (String keyProp: mapProp.keySet()) {
                if (mapProp.get(keyProp).getLong("propType")== FD_PropType_consts.attr ||
                        mapProp.get(keyProp).getLong("propType")== FD_PropType_consts.meter) {
                    if ( params.getLong("id" + keyProp.split("_")[1]) > 0 &&
                            !params.getString(keyProp.split("_")[1]).isEmpty() ) {
                        updateProperties(keyProp, params, mapProp.get(keyProp));
                    }
                    if ( params.getLong("id" + keyProp.split("_")[1]) == 0 &&
                            !params.getString(keyProp.split("_")[1]).isEmpty() ) {
                        fillProperties(keyProp, params, mapProp.get(keyProp));
                    }

                } else if (mapProp.get(keyProp).getLong("propType")== FD_PropType_consts.factor) {
                    if (params.getLong("id" + keyProp.split("_")[1]) > 0 &&
                            params.getLong("fv" + keyProp.split("_")[1]) > 0 ) {
                        updateProperties(keyProp, params, mapProp.get(keyProp));
                    }
                    if (params.getLong("id" + keyProp.split("_")[1]) == 0 &&
                            params.getLong("fv" + keyProp.split("_")[1]) > 0 ) {
                        fillProperties(keyProp, params, mapProp.get(keyProp));
                    }
                }  else if (mapProp.get(keyProp).getLong("propType")== FD_PropType_consts.typ) {
                    if (params.getLong("id" + keyProp.split("_")[1]) > 0 &&
                            params.getLong("obj" + keyProp.split("_")[1]) > 0 ) {
                        updateProperties(keyProp, params, mapProp.get(keyProp));
                    }
                    if (params.getLong("id" + keyProp.split("_")[1]) == 0 &&
                            params.getLong("obj" + keyProp.split("_")[1]) > 0 ) {
                        fillProperties(keyProp, params, mapProp.get(keyProp));
                    }
                }  else if (mapProp.get(keyProp).getLong("propType")== FD_PropType_consts.reltyp) {
                    if (params.getLong("id" + keyProp.split("_")[1]) > 0 &&
                            params.getLong("relobj" + keyProp.split("_")[1]) > 0 ) {
                        updateProperties(keyProp, params, mapProp.get(keyProp));
                    }
                    if (params.getLong("id" + keyProp.split("_")[1]) == 0 &&
                            params.getLong("relobj" + keyProp.split("_")[1]) > 0 ) {
                        fillProperties(keyProp, params, mapProp.get(keyProp));
                    }
                }  else if (mapProp.get(keyProp).getLong("propType")== FD_PropType_consts.measure) {
                    if (params.getLong("id" + keyProp.split("_")[1]) > 0 &&
                            params.getLong("mea" + keyProp.split("_")[1]) > 0 ) {
                        updateProperties(keyProp, params, mapProp.get(keyProp));
                    }
                    if (params.getLong("id" + keyProp.split("_")[1]) == 0 &&
                            params.getLong("mea" + keyProp.split("_")[1]) > 0 ) {
                        fillProperties(keyProp, params, mapProp.get(keyProp));
                    }
                }
            }
        } else {
            throw new XError("Unknown mode: " + mode);
        }
    }

    public void fillProperties(String cod, DbRec params, DbRec recProp) throws Exception {
        long own = params.getLong("own");
        String keyValue = cod.split("_")[1];
        long objRef = params.getLong("obj" + keyValue);
        long relobjRef = params.getLong("relobj" + keyValue);
        long propVal = params.getLong("pv" + keyValue);
        long prop = recProp.getLong("id");
        long propType = recProp.getLong("propType");
        long attribValType = recProp.getLong("attribValType");
        double koef = UtCnv.toDouble(recProp.get("koef"));
        if (koef == 0) koef = 1;
        Integer digit = null;
        if (recProp.get("digit") != null) digit = recProp.getInt("digit");
        //
        long idDP;
        UtEntityData ue1 = new UtEntityData(db, "DataProp");
        DbRec recDP = ue1.setDomain("DataProp", params);
        String whe = "and isObj=1 ";
        if (recProp.getLong("statusFactor") > 0) {
            long fv = recProp.getLong("fvStatus");
            whe += "and status = " + fv;
        } else {
            whe += "and status is null ";
        }
        if (recProp.getLong("providerTyp") > 0) {
            whe += "and provider is not null ";
        } else {
            whe += "and provider is null ";
        }
        List<DbRec> stDP = db.loadSql("""
            select id from DataProp
            where objOrRelObj=:own and prop=:prop
        """ + whe, Map.of("own", own, "prop", prop));

        if (!stDP.isEmpty()) {
            idDP = stDP.getFirst().getLong("id");
        } else {
            recDP.put("id", ue1.getNextId("DataProp"));
            recDP.put("isObj", 1);
            recDP.put("objOrRelObj", own);
            recDP.put("prop", prop);
            if (recProp.getLong("statusFactor") > 0) {
                long fv = recProp.getLong("fvStatus");
                recDP.put("status", fv);
            }
            if (recProp.getLong("providerTyp") > 0) {
                //todo
                // provider
                //
            }
            if (recProp.getBoolean("dependPeriod")) {
                recDP.put("periodType", FD_PeriodType_consts.year);
            }
            idDP = db.insertRec("DataProp", recDP);
        }
        DbRec recDPV = ue1.setDomain("DataPropVal", params);
        recDP.put("id", ue1.getNextId("DataPropVal"));
        recDPV.put("dataProp", idDP);

        // Attrib str
        if (FD_AttribValType_consts.str == attribValType) {
            //Необходимо добавить коды всех свойств типа [str]
            if (cod.equalsIgnoreCase("Prop_Specs") ||
                    cod.equalsIgnoreCase("Prop_LocationDetails") ||
                    cod.equalsIgnoreCase("Prop_Number") ||
                    cod.equalsIgnoreCase("Prop_BIN") ||
                    cod.equalsIgnoreCase("Prop_ContactPerson") ||
                    cod.equalsIgnoreCase("Prop_ContactDetails") ||
                    cod.equalsIgnoreCase("Prop_DocumentNumber") ||
                    cod.equalsIgnoreCase("Prop_DocumentAuthor")) {
                if (params.get(keyValue) != null || params.get(keyValue) != "") {
                    recDPV.put("strVal", params.getString(keyValue));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Attrib multiStr
        if (FD_AttribValType_consts.multistr == attribValType) {
            //Необходимо добавить коды всех свойств типа [multistr]
            if (cod.equalsIgnoreCase("Prop_Description")) {
                if (params.get(keyValue) != null || params.get(keyValue) != "") {
                    recDPV.put("multiStrVal", params.getString(keyValue));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Attrib dt
        if (FD_AttribValType_consts.dt == attribValType) {
            //Необходимо добавить коды всех свойств типа [dt]
            if (cod.equalsIgnoreCase("Prop_InstallationDate") ||
                    cod.equalsIgnoreCase("Prop_CreatedAt") ||
                    cod.equalsIgnoreCase("Prop_UpdatedAt") ||
                    cod.equalsIgnoreCase("Prop_DocumentApprovalDate") ||
                    cod.equalsIgnoreCase("Prop_DocumentStartDate") ||
                    cod.equalsIgnoreCase("Prop_DocumentEndDate")) {
                if (params.get(keyValue) != null || params.get(keyValue) != "") {
                    recDPV.put("dateTimeVal", LocalDate.parse(params.getString(keyValue), DateTimeFormatter.ISO_DATE));
                }
            } else throw new XError("for dev: [{0}] отсутствует в реализации", cod);
        }
        // FV
        if (FD_PropType_consts.factor == propType) {
            //Необходимо добавить коды всех свойств типа [factor]
            if (cod.equalsIgnoreCase("Prop_Side") ||
                    cod.equalsIgnoreCase("Prop_UserSex") ||
                    cod.equalsIgnoreCase("Prop_Status")) {
                if (propVal > 0) {
                    recDPV.put("propVal", propVal);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Measure
        if (FD_PropType_consts.measure == propType) {
            //Необходимо добавить коды всех свойств типа [measure]
            if (cod.equalsIgnoreCase("Prop_ParamsMeasure")) {
                if (propVal > 0) {
                    recDPV.put("propVal", propVal);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Meter
        double numberVal = 0;
        if (FD_PropType_consts.meter == propType || FD_PropType_consts.rate == propType) {
            //Необходимо добавить коды всех свойств типа [meter]
            if (cod.equalsIgnoreCase("Prop_StartKm") ||
                    cod.equalsIgnoreCase("Prop_StartPicket") ||
                    cod.equalsIgnoreCase("Prop_FinishKm") ||
                    cod.equalsIgnoreCase("Prop_FinishPicket") ||
                    cod.equalsIgnoreCase("Prop_PeriodicityReplacement")) {
                if (params.get(keyValue) != null || params.get(keyValue) != "") {
                    double v = UtCnv.toDouble(params.get(keyValue));
                    numberVal = v / koef;
                    if (digit != null) {
                        BigDecimal bd = new BigDecimal(numberVal);
                        bd = bd.setScale(digit, RoundingMode.HALF_UP);
                        numberVal = bd.doubleValue();
                    }
                    recDPV.put("numberVal", numberVal);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Typ
        if (FD_PropType_consts.typ == propType) {
            //Необходимо добавить коды всех свойств типа [typ]
            if (cod.equalsIgnoreCase("Prop_ObjectType") ||
                    cod.equalsIgnoreCase("Prop_Section") ||
                    cod.equalsIgnoreCase("Prop_User") ||
                    cod.equalsIgnoreCase("Prop_LocationClsSection") ||
                        cod.equalsIgnoreCase("Prop_LocationMulti")) {
                if (objRef > 0) {
                    recDPV.put("propVal", propVal);
                    recDPV.put("obj", objRef);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // RelTyp
        if (FD_PropType_consts.reltyp == propType) {
            //Необходимо добавить коды всех свойств типа [typ]
            if (cod.equalsIgnoreCase("Prop_ComponentParams")) { //for template
                if (relobjRef > 0) {
                    recDPV.put("propVal", propVal);
                    recDPV.put("relobj", relobjRef);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        //
        if (recDP.getLong("periodType") > 0) {
            if (!params.containsKey("dte"))
                params.put("dte", LocalDate.parse(params.getString(keyValue), DateTimeFormatter.ISO_DATE));

            UtPeriod utPeriod = new UtPeriod();
            LocalDate d1 = utPeriod.calcDbeg(LocalDate.parse(params.getString("dte"), DateTimeFormatter.ISO_DATE), recDP.getLong("periodType"), 0);
            LocalDate d2 = utPeriod.calcDend(LocalDate.parse(params.getString("dte"), DateTimeFormatter.ISO_DATE), recDP.getLong("periodType"), 0);
            recDPV.put("dbeg", d1);
            recDPV.put("dend", d2);

        } else {
            recDPV.put("dbeg", LocalDate.parse("1800-01-01", DateTimeFormatter.ISO_DATE));
            recDPV.put("dend", LocalDate.parse("3333-12-31", DateTimeFormatter.ISO_DATE));
        }
        //
        long au = getUser();
        recDPV.put("authUser", au);
        recDPV.put("inputType", FD_InputType_consts.app);
        long idDPV = ue1.getNextId("DataPropVal");
        recDPV.put("id", idDPV);
        recDPV.put("ord", idDPV);
        recDPV.put("timeStamp", LocalDate.now());
        db.insertRec("DataPropVal", recDPV);
    }

    private void updateProperties(String cod, DbRec params, DbRec recProp) throws Exception {
        String keyValue = cod.split("_")[1];
        long idVal = params.getLong("id" + keyValue);
        long objRef = params.getLong("obj" + keyValue);
        long relobjRef = params.getLong("relobj" + keyValue);
        long propVal = params.getLong("pv" + keyValue);
        long propType = recProp.getLong("propType");
        long attribValType = recProp.getLong("attribValType");
        double koef = UtCnv.toDouble(recProp.get("koef"));
        if (koef == 0) koef = 1;
        Integer digit = null;
        if (recProp.get("digit") != null) digit = recProp.getInt("digit");
        //
        DbRec recDPV = db.loadRec("DataPropVal", idVal);
        String strValue = params.getString(keyValue);
        // Attrib str
        if (FD_AttribValType_consts.str == attribValType) {
            //Необходимо добавить коды всех свойств типа [str]
            if (cod.equalsIgnoreCase("Prop_Specs") ||
                    cod.equalsIgnoreCase("Prop_LocationDetails") ||
                    cod.equalsIgnoreCase("Prop_Number") ||
                    cod.equalsIgnoreCase("Prop_BIN") ||
                    cod.equalsIgnoreCase("Prop_ContactPerson") ||
                    cod.equalsIgnoreCase("Prop_ContactDetails") ||
                    cod.equalsIgnoreCase("Prop_DocumentNumber") ||
                    cod.equalsIgnoreCase("Prop_DocumentAuthor")) {
                if (!params.containsKey(keyValue) || strValue.trim().isEmpty()) {
                    db.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, Map.of("idVal", idVal));
                } else {
                    recDPV.put("strval", params.getString(keyValue));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Attrib multiStr
        if (FD_AttribValType_consts.multistr == attribValType) {
            //Необходимо добавить коды всех свойств типа [multistr]
            if (cod.equalsIgnoreCase("Prop_Description")) {
                if (!params.containsKey(keyValue) || strValue.trim().isEmpty()) {
                    db.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, Map.of("idVal", idVal));
                } else {
                    recDPV.put("multistrval", params.getString(keyValue));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        //Attrib dt
        if (FD_AttribValType_consts.dt == attribValType) {
            //Необходимо добавить коды всех свойств типа [dt]
            if (cod.equalsIgnoreCase("Prop_InstallationDate") ||
                    cod.equalsIgnoreCase("Prop_CreatedAt") ||
                    cod.equalsIgnoreCase("Prop_UpdatedAt") ||
                    cod.equalsIgnoreCase("Prop_DocumentApprovalDate") ||
                    cod.equalsIgnoreCase("Prop_DocumentStartDate") ||
                    cod.equalsIgnoreCase("Prop_DocumentEndDate")) {
                if (!params.containsKey(keyValue) || strValue.trim().isEmpty()) {
                    db.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, Map.of("idVal", idVal));
                } else {
                    recDPV.put("datetimeval", LocalDate.parse(params.getString(keyValue), DateTimeFormatter.ISO_DATE));
                }
            } else
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
        }

        // FV
        if (FD_PropType_consts.factor == propType) {
            //Необходимо добавить коды всех свойств типа [factor]
            if (cod.equalsIgnoreCase("Prop_Side") ||
                    cod.equalsIgnoreCase("Prop_UserSex") ||
                    cod.equalsIgnoreCase("Prop_Status")) {
                if (propVal > 0) {
                    recDPV.put("propval", propVal);
                } else {
                    db.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, Map.of("idVal", idVal));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }

        // Measure
        if (FD_PropType_consts.measure == propType) {
            //Необходимо добавить коды всех свойств типа [measure]
            if (cod.equalsIgnoreCase("Prop_ParamsMeasure")) {
                if (propVal > 0)
                    recDPV.put("propval", propVal);
                else {
                    db.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, Map.of("idVal", idVal));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Meter
        double numberVal = 0;
        if (FD_PropType_consts.meter == propType || FD_PropType_consts.rate == propType) {
            //Необходимо добавить коды всех свойств типа [meter]
            if (cod.equalsIgnoreCase("Prop_StartKm") ||
                    cod.equalsIgnoreCase("Prop_StartPicket") ||
                    cod.equalsIgnoreCase("Prop_FinishKm") ||
                    cod.equalsIgnoreCase("Prop_FinishPicket") ||
                    cod.equalsIgnoreCase("Prop_PeriodicityReplacement")) {
                if (!params.getString(keyValue).isEmpty()) {
                    double v = UtCnv.toDouble(params.get(keyValue));
                    numberVal = v / koef;
                    if (digit != null) {
                        BigDecimal bd = new BigDecimal(numberVal);
                        bd = bd.setScale(digit, RoundingMode.HALF_UP);
                        numberVal = bd.doubleValue();
                    }
                    recDPV.put("numberval", numberVal);
                } else {
                    db.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, Map.of("idVal", idVal));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Typ
        if (FD_PropType_consts.typ == propType) {
            //Необходимо добавить коды всех свойств типа [typ]
            if (cod.equalsIgnoreCase("Prop_ObjectType") ||
                    cod.equalsIgnoreCase("Prop_Section") ||
                    cod.equalsIgnoreCase("Prop_User") ||
                    cod.equalsIgnoreCase("Prop_LocationClsSection") ||
                        cod.equalsIgnoreCase("Prop_LocationMulti")) {
                if (objRef > 0) {
                    recDPV.put("propval", propVal);
                    recDPV.put("obj", objRef);
                } else {
                    db.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, Map.of("idVal", idVal));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // RelTyp
        if (FD_PropType_consts.reltyp == propType) {
            //Необходимо добавить коды всех свойств типа [reltyp]
            if (cod.equalsIgnoreCase("Prop_ComponentParams")) { //for template
                if (relobjRef > 0) {
                    recDPV.put("propVal", propVal);
                    recDPV.put("relobj", relobjRef);
                } else {
                    db.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, Map.of("idVal", idVal));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        //
        long au = getUser();
        recDPV.put("authuser", au);
        recDPV.put("inputtype", FD_InputType_consts.app);
        recDPV.put("timestamp", LocalDate.now());
        db.updateRec("DataPropVal", recDPV);
    }


    private void checkCod(String cod) throws Exception {
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
    private void checkCodUnique(String cod) throws Exception {
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

    private long getUser() throws Exception {
        //AuthService authSvc = mdb.getApp().bean(AuthService.class);
        long au = 1; //todo authSvc.getCurrentUser().getAttrs().getLong("id");
        //if (au == 0)
        //    au = 1//throw new XError("notLogined")
        return au;
    }


}
