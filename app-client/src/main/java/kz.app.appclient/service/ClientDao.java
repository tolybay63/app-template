package kz.app.appclient.service;


import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtCnv;
import kz.app.appcore.utils.UtPeriod;
import kz.app.appcore.utils.XError;
import kz.app.appcore.utils.consts.FD_AttribValType_consts;
import kz.app.appcore.utils.consts.FD_InputType_consts;
import kz.app.appcore.utils.consts.FD_PeriodType_consts;
import kz.app.appcore.utils.consts.FD_PropType_consts;
import kz.app.appdata.service.UtEntityData;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@Component
public class ClientDao {

    private final Db db;
    private final MetaDao metaDao;

    public ClientDao(Db db, MetaDao metaDao) {
        this.db = db;
        this.metaDao = metaDao;
    }


    public List<DbRec> loadClient(long id) throws Exception {

        DbRec map = metaDao.getIdFromCodOfEntity("Cls", "Cls_Client", "");
        long cls = UtCnv.toLong(map.get("Cls_Client"));

        String whe = "o.id=:id";
        if (id == 0) whe = "o.cls=:Cls_Client";

        //map = apiMeta().get(ApiMeta).getIdFromCodOfEntity("Prop", "", "Prop_%")
        map = metaDao.getIdFromCodOfEntity("Prop", "", "Prop_%");
        map.put("Cls_Client", cls);
        //
/*
        DbRec map = new DbRec();
        map.put("Cls_Client", 1126);
        map.put("Prop_BIN", 1168);
        map.put("Prop_ContactPerson", 1169);
        map.put("Prop_ContactDetails", 1170);
        map.put("Prop_Description", 1137);
*/

        //

        return db.loadSql("""
                    select o.id, o.cls, v.name,
                        v1.id as idBIN, v1.strVal as BIN,
                        v2.id as idContactPerson, v2.strVal as ContactPerson,
                        v3.id as idContactDetails, v3.strVal as ContactDetails,
                        v4.id as idDescription, v4.multiStrVal as Description
                    from Obj o
                        left join ObjVer v on o.id=v.ownerVer and v.lastVer=1
                        left join DataProp d1 on d1.objorrelobj=o.id and d1.prop=:Prop_BIN  --1168
                        left join DataPropVal v1 on d1.id=v1.dataprop
                        left join DataProp d2 on d2.objorrelobj=o.id and d2.prop=:Prop_ContactPerson    --1169
                        left join DataPropVal v2 on d2.id=v2.dataprop
                        left join DataProp d3 on d3.objorrelobj=o.id and d3.prop=:Prop_ContactDetails   --1170
                        left join DataPropVal v3 on d3.id=v3.dataprop
                        left join DataProp d4 on d4.objorrelobj=o.id and d4.prop=:Prop_Description  --1137
                        left join DataPropVal v4 on d4.id=v4.dataprop
                    where
                """ + whe, map);
    }

    private void validateForDelete(long id, int isObj) throws Exception {
        if (isObj == 1) {
            //...
        } else {
            //...
        }
    }

    public void deleteClientWithProps(long id) throws Exception {
        validateForDelete(id, 1);
        UtEntityData ue = new UtEntityData(db, "Obj");
        ue.deleteEntity(id);
    }

    public List<DbRec> saveClient(String mode, DbRec params) throws Exception {
        long own;
        UtEntityData ue = new UtEntityData(db, "Obj");

        if (UtCnv.toString(params.get("name")).trim().isEmpty()) throw new XError("[name] не указан");
        DbRec par = new DbRec(params);
        par.put("cls", 1126);
        if (mode.equalsIgnoreCase("ins")) {
            //todo Map<String, Long> map = apiMeta().get(ApiMeta).getIdFromCodOfEntity("Cls", "Cls_Client", "")
            //par.put("cls", 1126);
            par.putIfAbsent("fullname", par.get("name"));
            own = ue.insertEntity(par);
            //...
            params.put("own", own);
            //1 Prop_BIN
            if (params.getString("BIN").isEmpty()) throw new XError("[BIN] не указан");
            else fillProperties(true, "Prop_BIN", params);
            //2 Prop_ContactPerson
            if (params.getString("ContactPerson").isEmpty()) throw new XError("[ContactPerson] не указан");
            else fillProperties(true, "Prop_ContactPerson", params);
            //3 Prop_ContactDetails
            if (params.getString("ContactDetails").isEmpty()) throw new XError("[ContactDetails] не указан");
            else fillProperties(true, "Prop_ContactDetails", params);
            //4 Prop_Description
            if (!params.getString("Description").isEmpty()) fillProperties(true, "Prop_Description", params);
        } else if (mode.equalsIgnoreCase("upd")) {
            own = params.getLong("id");
            par.putIfAbsent("fullname", par.get("name"));
            ue.updateEntity(par);
            //...

        } else {
            throw new XError("Unknown mode: " + mode);
        }
        //
        return loadClient(own);

    }

    private void fillProperties(boolean isObj, String cod, DbRec params) throws Exception {
        long own = params.getLong("own");
        String keyValue = cod.split("_")[1];
        long objRef = params.getLong("obj" + keyValue);
        long propVal = params.getLong("pv" + keyValue);

        //List<DbRec> stProp = new ArrayList<DbRec>();//todo
        List<DbRec> stProp = metaDao.getPropInfo(cod);
        //
        long prop = stProp.getFirst().getLong("id");
        long propType = stProp.getFirst().getLong("propType");
        long attribValType = stProp.getFirst().getLong("attribValType");
        Integer digit = null;
        double koef = UtCnv.toDouble(stProp.getFirst().get("koef"));
        if (koef == 0) koef = 1;
        if (stProp.getFirst().get("digit") != null) digit = stProp.getFirst().getInt("digit");

        //
        long idDP;
        UtEntityData ue = new UtEntityData(db, "DataProp");
        DbRec recDP = ue.setDomain("DataProp", params);
        String whe = isObj ? "and isObj=1 " : "and isObj=0 ";
        if (stProp.getFirst().getLong("statusFactor") > 0) {
            long fv = metaDao.getDefaultStatus(prop); //todo apiMeta().get(ApiMeta).getDefaultStatus(prop)
            whe += "and status = " + fv;
        } else {
            whe += "and status is null ";
        }
        whe += " and provider is null ";
        //todo if (stProp.get(0).getLong("providerTyp") > 0)

        if (stProp.getFirst().getLong("providerTyp") > 0) {
            whe += "and periodType is not null ";
        } else {
            whe += "and periodType is null";
        }
        List<DbRec> stDP = db.loadSql("""
                    select id from DataProp
                    where objOrRelObj=:own and prop=:prop
                """ + whe, Map.of("own", own, "prop", prop));
        if (!stDP.isEmpty()) {
            idDP = stDP.getFirst().getLong("id");
        } else {
            recDP.put("isObj", isObj);
            recDP.put("objOrRelObj", own);
            recDP.put("prop", prop);
            if (stProp.getFirst().getLong("statusFactor") > 0) {
                long fv = metaDao.getDefaultStatus(prop); //todo apiMeta().get(ApiMeta).getDefaultStatus(prop);
                recDP.put("status", fv);
            }
            if (stProp.getFirst().getLong("providerTyp") > 0) {
                //todo
                // provider
                //
            }
            if (stProp.getFirst().getBoolean("dependPeriod")) {
                recDP.put("periodType", FD_PeriodType_consts.year);
            }
            idDP = db.insertRec("DataProp", recDP);
        }
        //
        //StoreRecord recDPV = mdb.createStoreRecord("DataPropVal")
        DbRec recDPV = ue.setDomain("DataPropVal", params);
        recDPV.put("dataProp", idDP);
        // Attrib
        if (FD_AttribValType_consts.str == attribValType) {
            if (cod.equalsIgnoreCase("Prop_BIN") || cod.equalsIgnoreCase("Prop_ContactPerson") || cod.equalsIgnoreCase("Prop_ContactDetails")) {
                if (params.get(keyValue) != null || params.get(keyValue) != "") {
                    recDPV.put("strVal", params.getString(keyValue));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        //
        if (FD_AttribValType_consts.multistr == attribValType) {
            if (cod.equalsIgnoreCase("Prop_Description")) {
                if (params.get(keyValue) != null || params.get(keyValue) != "") {
                    recDPV.put("multiStrVal", params.getString(keyValue));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }

        if (FD_AttribValType_consts.dt == attribValType) {
            if (cod.equalsIgnoreCase("Prop_CreatedAt")) {
                if (params.get(keyValue) != null || params.get(keyValue) != "") {
                    recDPV.put("dateTimeVal", LocalDate.parse(params.getString(keyValue), DateTimeFormatter.ISO_DATE));
                }
            } else throw new XError("for dev: [{0}] отсутствует в реализации", cod);
        }

        // For FV
        if (FD_PropType_consts.factor == propType) {
            if (cod.equalsIgnoreCase("Prop_UserSex")) {    //template
                if (propVal > 0) {
                    recDPV.put("propVal", propVal);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }

        // For Measure
        if (FD_PropType_consts.measure == propType) {
            if (cod.equalsIgnoreCase("Prop_ParamsMeasure")) {
                if (propVal > 0) {
                    recDPV.put("propVal", propVal);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }

        // For Meter
        if (FD_PropType_consts.meter == propType || FD_PropType_consts.rate == propType) {
            if (cod.equalsIgnoreCase("Prop_StartKm")) {
                if (params.get(keyValue) != null || params.get(keyValue) != "") {
                    var v = UtCnv.toDouble(params.get(keyValue));
                    v = v / koef;
                    if (digit != null) {
                        String vf = new DecimalFormat("#0.00").format(v);
                        v = Double.parseDouble(vf);
                    }
                    recDPV.put("numberVal", v);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        if (FD_PropType_consts.typ == propType) {
            if (cod.equalsIgnoreCase("Prop_LocationClsSection")) {
                if (objRef > 0) {
                    recDPV.put("propVal", propVal);
                    recDPV.put("obj", objRef);
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

        long au = getUser();
        recDPV.put("authUser", au);
        recDPV.put("inputType", FD_InputType_consts.app);
        long idDPV = ue.getNextId("DataPropVal");
        recDPV.put("id", idDPV);
        recDPV.put("ord", idDPV);
        recDPV.put("timeStamp", LocalDate.now());
        db.insertRec("DataPropVal", recDPV);
    }

    private long getUser() throws Exception {
        //AuthService authSvc = mdb.getApp().bean(AuthService.class);
        long au = 1; //todo authSvc.getCurrentUser().getAttrs().getLong("id");
        //if (au == 0)
        //    au = 1//throw new XError("notLogined")
        return au;
    }


}
