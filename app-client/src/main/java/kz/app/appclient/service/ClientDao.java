package kz.app.appclient.service;


import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.*;
import kz.app.appcore.utils.consts.FD_AttribValType_consts;
import kz.app.appcore.utils.consts.FD_InputType_consts;
import kz.app.appcore.utils.consts.FD_PeriodType_consts;
import kz.app.appcore.utils.consts.FD_PropType_consts;
import kz.app.appdata.service.UtEntityData;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import kz.app.appnsi.service.NsiDao;
import kz.app.appobject.service.ObjectDao;
import kz.app.apppersonnal.service.PersonnalDao;
import kz.app.appplan.service.PlanDao;
import kz.app.structure.service.StructureDao;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class ClientDao {

    private final Db dbClient;

    private final MetaDao metaService;
    private final ObjectDao objectService;
    private final PlanDao planService;
    private final StructureDao structureService;
    private final NsiDao nsiService;
    private final PersonnalDao personnalService;

    public ClientDao(@Qualifier("dbClient") Db dbClient, MetaDao metaService, ObjectDao objectService,
                     PlanDao planService, StructureDao structureService, NsiDao nsiService, PersonnalDao personnalService) {
        this.dbClient = dbClient;
        this.metaService = metaService;
        this.objectService = objectService;
        this.planService = planService;
        this.structureService = structureService;
        this.nsiService = nsiService;
        this.personnalService = personnalService;
    }


    public List<DbRec> loadClient(long id) throws Exception {

        DbRec map = metaService.getIdFromCodOfEntity("Cls", "Cls_Client", "");
        long cls = UtCnv.toLong(map.get("Cls_Client"));
        map = metaService.getIdFromCodOfEntity("Prop", "", "Prop_%");
        String whe = "o.id=:id";
        if (id == 0) {
            whe = "o.cls=:Cls_Client";
            map.put("Cls_Client", cls);
        } else {
            map.put("id", id);
        }
        //
        return dbClient.loadSql("""
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

    private void validateForDeleteObj(long owner) throws Exception {
        String sql = "select o.cls, v.name from Obj o, ObjVer v where o.id=v.ownerVer and v.lastVer=1 and o.id=:id";
        DbRec recOwn = dbClient.loadSqlRec(sql, Map.of("id", owner), false);
        if (recOwn != null) {
            List<String> lstService = new ArrayList<>();
            String name = recOwn.getString("name");
            long cls = recOwn.getLong("cls");
            Set<Long> stPV = metaService.getIdsPV(1, cls, "");
            if (!(stPV.size()==1 && stPV.contains(0L))) {
                String whePV = "(" + UtString.join(stPV, ",") + ")";
                //clientdata
                List<DbRec> st = getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("clientdata");
                }
                //objectdata
                st =  objectService.getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("objectdata");
                }
                //plandata
                st =  planService.getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("plandata");
                }
                //personnaldata
                st =  personnalService.getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("personnaldata");
                }
                //nsidata
                st =  nsiService.getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("nsidata");
                }
                //structuredata
                st =  structureService.getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("structuredata");
                }
                //...
                if (!lstService.isEmpty()) {
                    throw new XError("{0} используется в [{0}]", name, UtString.join(lstService, ", "));
                }
            }
        }
    }

    //todo Метод должен быть во всех data-сервисах
    public List<DbRec> getRefData(int isObj, long owner, String whePV) throws Exception {
        return dbClient.loadSql("""
                    select d.id from DataProp d, DataPropVal v
                    where d.id=v.dataProp and d.isObj=:isObj and v.propVal in
        """ + whePV + " and obj=:owner", Map.of("isObj", isObj, "owner", owner));
    }

    public void deleteClientWithProps(long id) throws Exception {
        validateForDeleteObj(id);
        UtEntityData ue = new UtEntityData(dbClient, "Obj");
        ue.deleteObjWithProps(id);
    }

    public List<DbRec> saveClient(String mode, DbRec params) throws Exception {
        long own;
        UtEntityData ue = new UtEntityData(dbClient, "Obj");

        DbRec par = new DbRec(params);
        if (mode.equalsIgnoreCase("ins")) {
            if (UtCnv.toString(params.get("name")).trim().isEmpty()) throw new XError("[name] не указан");
            DbRec map = metaService.getIdFromCodOfEntity("Cls", "Cls_Client", "");
            par.put("cls", map.getLong("Cls_Client"));
            par.putIfAbsent("fullname", par.get("name"));
            own = ue.insertEntity(par);
            //...
            params.put("own", own);
            //1 Prop_BIN
            if (params.getString("BIN").isEmpty()) throw new XError("[BIN] не указан");
            else fillProperties(1, "Prop_BIN", params);
            //2 Prop_ContactPerson
            if (params.getString("ContactPerson").isEmpty()) throw new XError("[ContactPerson] не указан");
            else fillProperties(1, "Prop_ContactPerson", params);
            //3 Prop_ContactDetails
            if (params.getString("ContactDetails").isEmpty()) throw new XError("[ContactDetails] не указан");
            else fillProperties(1, "Prop_ContactDetails", params);
            //4 Prop_Description
            if (!params.getString("Description").isEmpty()) fillProperties(1, "Prop_Description", params);
        } else if (mode.equalsIgnoreCase("upd")) {
            own = params.getLong("id");
            ue.updateEntity(par);
            //...
            //
            params.put("own", own);
            //1 Prop_BIN
            if (params.containsKey("idBIN"))
                updateProperties("Prop_BIN", params);
            //2 Prop_ContactPerson
            if (params.containsKey("idContactPerson"))
                updateProperties("Prop_ContactPerson", params);
            //3 Prop_ContactDetails
            if (params.containsKey("idContactDetails"))
                updateProperties("Prop_ContactDetails", params);
            //4 Prop_Description
            if (params.containsKey("idDescription"))
                updateProperties("Prop_Description", params);
            else {
                if (!params.getString("Description").isEmpty())
                    fillProperties(1, "Prop_Description", params);
            }
        } else {
            throw new XError("Unknown mode: " + mode);
        }
        //
        return loadClient(own);

    }

    private void fillProperties(int isObj, String cod, DbRec params) throws Exception {

        long own = params.getLong("own");
        String keyValue = cod.split("_")[1];
        long objRef = params.getLong("obj" + keyValue);
        long propVal = params.getLong("pv" + keyValue);
        //
        List<DbRec> stProp = metaService.getPropInfo(cod);
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
        UtEntityData ue = new UtEntityData(dbClient, "DataProp");
        DbRec recDP = ue.setDomain("DataProp", params);
        String whe = "and isObj="+ isObj+" ";
        if (stProp.getFirst().getLong("statusFactor") > 0) {
            long fv = metaService.getDefaultStatus(prop);
            whe += "and status = " + fv;
        } else {
            whe += "and status is null ";
        }
        whe += " and provider is null ";

        if (stProp.getFirst().getLong("providerTyp") > 0) {
            whe += "and periodType is not null ";
        } else {
            whe += "and periodType is null ";
        }
        List<DbRec> stDP = dbClient.loadSql("""
                    select id from DataProp
                    where objOrRelObj=:own and prop=:prop
                """ + whe, Map.of("own", own, "prop", prop));
        if (!stDP.isEmpty()) {
            idDP = stDP.getFirst().getLong("id");
        } else {
            recDP.put("id", ue.getNextId("DataProp"));
            recDP.put("isObj", isObj);
            recDP.put("objOrRelObj", own);
            recDP.put("prop", prop);
            if (stProp.getFirst().getLong("statusFactor") > 0) {
                long fv = metaService.getDefaultStatus(prop);
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
            idDP = dbClient.insertRec("DataProp", recDP);
        }
        //
        DbRec recDPV = ue.setDomain("DataPropVal", params);
        recDP.put("id", ue.getNextId("DataPropVal"));
        recDPV.put("dataProp", idDP);
        // Attrib str
        if (FD_AttribValType_consts.str == attribValType) {
            if (cod.equalsIgnoreCase("Prop_BIN") ||
                    cod.equalsIgnoreCase("Prop_ContactPerson") ||
                        cod.equalsIgnoreCase("Prop_ContactDetails")) {
                if (params.get(keyValue) != null || params.get(keyValue) != "") {
                    recDPV.put("strVal", params.getString(keyValue));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Attrib multiStr
        if (FD_AttribValType_consts.multistr == attribValType) {
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
            if (cod.equalsIgnoreCase("Prop_CreatedAt")) {
                if (params.get(keyValue) != null || params.get(keyValue) != "") {
                    recDPV.put("dateTimeVal", LocalDate.parse(params.getString(keyValue), DateTimeFormatter.ISO_DATE));
                }
            } else throw new XError("for dev: [{0}] отсутствует в реализации", cod);
        }
        // FV
        if (FD_PropType_consts.factor == propType) {
            if (cod.equalsIgnoreCase("Prop_UserSex")) {    //template
                if (propVal > 0) {
                    recDPV.put("propVal", propVal);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Measure
        if (FD_PropType_consts.measure == propType) {
            if (cod.equalsIgnoreCase("Prop_ParamsMeasure")) {
                if (propVal > 0) {
                    recDPV.put("propVal", propVal);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Meter
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
        // Typ
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
        //
        long au = getUser();
        recDPV.put("authUser", au);
        recDPV.put("inputType", FD_InputType_consts.app);
        long idDPV = ue.getNextId("DataPropVal");
        recDPV.put("id", idDPV);
        recDPV.put("ord", idDPV);
        recDPV.put("timeStamp", LocalDate.now());
        dbClient.insertRec("DataPropVal", recDPV);
    }

    private void updateProperties(String cod, DbRec params) throws Exception {
        //VariantMap mapProp = new VariantMap(params)
        String keyValue = cod.split("_")[1];
        long idVal = params.getLong("id" + keyValue);
        long propVal = params.getLong("pv" + keyValue);
        long objRef = params.getLong("obj" + keyValue);

        //Store stProp = apiMeta().get(ApiMeta).getPropInfo(cod)
        List<DbRec> stProp = metaService.getPropInfo(cod);

        //
        long propType = stProp.getFirst().getLong("propType");
        long attribValType = stProp.getFirst().getLong("attribValType");
        Integer digit = null;
        double koef = UtCnv.toDouble(stProp.getFirst().get("koef"));
        if (koef == 0) koef = 1;
        if (stProp.getFirst().get("digit") != null) digit = stProp.getFirst().getInt("digit");
        String sql = "";
        //def tmst = XDateTime.create(new Date()).toString(XDateTimeFormatter.ISO_DATE_TIME)
        LocalDateTime tmst = LocalDateTime.now();
        String strValue = params.getString(keyValue);
        // For Attrib
        if (FD_AttribValType_consts.str == attribValType) {
            if (cod.equalsIgnoreCase("Prop_BIN") ||
                    cod.equalsIgnoreCase("Prop_ContactPerson") ||
                    cod.equalsIgnoreCase("Prop_ContactDetails")) {   //For Template
                if (!params.containsKey(keyValue) || strValue.trim().isEmpty()) {
                    sql = """
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """;
                } else {
                    sql = "update DataPropVal set strVal=:strValue, timeStamp=:tmst where id=:idVal";
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }

        if (FD_AttribValType_consts.multistr == attribValType) {
            if (cod.equalsIgnoreCase("Prop_Description")) {
                if (!params.containsKey(keyValue) || strValue.trim().isEmpty()) {
                    sql = """
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """;
                } else {
                    sql = "update DataPropVal set multiStrVal=:strValue, timeStamp=:tmst where id=:idVal";
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }

        if (FD_AttribValType_consts.dt == attribValType) {
            if (cod.equalsIgnoreCase("Prop_CreatedAt")) {
                if (!params.containsKey(keyValue) || strValue.trim().isEmpty()) {
                    sql = """
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """;
                } else {
                    sql = "update DataPropVal set dateTimeVal=:strValue, timeStamp=:tmst where id=:idVal";
                }
            } else
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
        }

        // For FV
        if (FD_PropType_consts.factor == propType) {
            if (cod.equalsIgnoreCase("Prop_UserSex")) {    //template
                if (propVal > 0)
                    sql = "update DataPropVal set propVal=:propVal, timeStamp=:tmst where id=:idVal";
                else {
                    sql = """
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """;
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }

        // For Measure
        if (FD_PropType_consts.measure == propType) {
            if (cod.equalsIgnoreCase("Prop_ParamsMeasure")) {
                if (propVal > 0)
                    sql = "update DataPropVal set propVal=:propVal, timeStamp=:tmst where id=:idVal";
                else {
                    sql = """
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """;
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }

        double numberVal = 0;
        if (FD_PropType_consts.meter == propType || FD_PropType_consts.rate == propType) {
            if (cod.equalsIgnoreCase("Prop_StartKm")) {
                if (!params.getString(keyValue).isEmpty()) {
                    double v = UtCnv.toDouble(params.get(keyValue));
                    numberVal = v / koef;
                    if (digit != null) {
                        BigDecimal bd = new BigDecimal(numberVal);
                        bd = bd.setScale(digit, RoundingMode.HALF_UP);
                        numberVal = bd.doubleValue();
                    }
                    sql = "update DataPropVal set numberVal=:numberVal, timeStamp=:tmst where id=:idVal";
                } else {
                    sql = """
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """;
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // For Typ
        if (FD_PropType_consts.typ == propType) {
            if (cod.equalsIgnoreCase("Prop_User")) {
                if (objRef > 0)
                    sql = "update DataPropVal set propVal=:propVal, obj=:objRef, timeStamp=:tmst where id=:idVal";
                else {
                    sql = """
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """;
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }

        dbClient.execSql(sql, Map.of("idVal", idVal, "strValue", strValue, "tmst", tmst,
                "propVal", propVal, "objRef", objRef, "numberVal", numberVal));
    }


    private long getUser() throws Exception {
        //AuthService authSvc = mdb.getApp().bean(AuthService.class);
        long au = 1; //todo authSvc.getCurrentUser().getAttrs().getLong("id");
        //if (au == 0)
        //    au = 1//throw new XError("notLogined")
        return au;
    }


}
