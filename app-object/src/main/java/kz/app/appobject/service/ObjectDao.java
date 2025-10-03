package kz.app.appobject.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtCnv;
import kz.app.appcore.utils.UtDb;
import kz.app.appcore.utils.UtPeriod;
import kz.app.appcore.utils.XError;
import kz.app.appcore.utils.consts.FD_AttribValType_consts;
import kz.app.appcore.utils.consts.FD_InputType_consts;
import kz.app.appcore.utils.consts.FD_PeriodType_consts;
import kz.app.appcore.utils.consts.FD_PropType_consts;
import kz.app.appdata.service.UtEntityData;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import kz.app.appnsi.service.NsiDao;
import kz.app.structure.service.StructureDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@Component
public class ObjectDao {

    private final Db dbObject;
    private final MetaDao metaService;
    private final NsiDao nsiService;
    private final StructureDao structureService;

    public ObjectDao(@Qualifier("dbObject") Db dbObject, MetaDao metaService, NsiDao nsiService, StructureDao structureService) {
        this.dbObject = dbObject;
        this.metaService = metaService;
        this.nsiService = nsiService;
        this.structureService = structureService;
    }

    public List<DbRec> loadObjectServed(long id) throws Exception {
        String idsCls = metaService.getIdsCls("Typ_Object");

        DbRec map = metaService.getIdFromCodOfEntity("Prop", "", "Prop_%");
        String whe = " o.id=" + id;
        if (id == 0)
            whe = " o.cls in " + idsCls;

        List<DbRec> st = dbObject.loadSql("""
            select o.id, o.cls, v.name, v.fullName,
            v1.id as idObjectType, v1.propVal as pvObjectType, v1.obj as objObjectType, null as nameObjectType,
            v2.id as idStartKm, v2.numberVal as StartKm,
            v3.id as idFinishKm, v3.numberVal as FinishKm,
            v4.id as idStartPicket, v4.numberVal as StartPicket,
            v5.id as idFinishPicket, v5.numberVal as FinishPicket,
            v6.id as idPeriodicityReplacement, v6.numberVal as PeriodicityReplacement,
            v7.id as idSide, v7.propVal as pvSide, null as fvSide, null as nameSide,
            v8.id as idSpecs, v8.strVal as Specs,
            v9.id as idLocationDetails, v9.strVal as LocationDetails,
            v10.id as idNumber, v10.strVal as Number,
            v11.id as idInstallationDate, v11.dateTimeVal as InstallationDate,
            v12.id as idCreatedAt, v12.dateTimeVal as CreatedAt,
            v13.id as idUpdatedAt, v13.dateTimeVal as UpdatedAt,
            v14.id as idDescription, v14.multiStrVal as Description,
            v15.id as idSection, v15.propVal as pvSection, v15.obj as objSection, ov15.name as nameSection
        from Obj o
            left join ObjVer v on o.id=v.ownerver and v.lastver=1
            left join DataProp d1 on d1.objorrelobj=o.id and d1.prop=:Prop_ObjectType
            left join DataPropVal v1 on d1.id=v1.dataprop
            left join DataProp d2 on d2.objorrelobj=o.id and d2.prop=:Prop_StartKm
            left join DataPropVal v2 on d2.id=v2.dataprop
            left join DataProp d3 on d3.objorrelobj=o.id and d3.prop=:Prop_FinishKm
            left join DataPropVal v3 on d3.id=v3.dataprop
            left join DataProp d4 on d4.objorrelobj=o.id and d4.prop=:Prop_StartPicket
            left join DataPropVal v4 on d4.id=v4.dataprop
            left join DataProp d5 on d5.objorrelobj=o.id and d5.prop=:Prop_FinishPicket
            left join DataPropVal v5 on d5.id=v5.dataprop
            left join DataProp d6 on d6.objorrelobj=o.id and d6.prop=:Prop_PeriodicityReplacement
            left join DataPropVal v6 on d6.id=v6.dataprop
            left join DataProp d7 on d7.objorrelobj=o.id and d7.prop=:Prop_Side
            left join DataPropVal v7 on d7.id=v7.dataprop
            left join DataProp d8 on d8.objorrelobj=o.id and d8.prop=:Prop_Specs
            left join DataPropVal v8 on d8.id=v8.dataprop
            left join DataProp d9 on d9.objorrelobj=o.id and d9.prop=:Prop_LocationDetails
            left join DataPropVal v9 on d9.id=v9.dataprop
            left join DataProp d10 on d10.objorrelobj=o.id and d10.prop=:Prop_Number
            left join DataPropVal v10 on d10.id=v10.dataprop
            left join DataProp d11 on d11.objorrelobj=o.id and d11.prop=:Prop_InstallationDate
            left join DataPropVal v11 on d11.id=v11.dataprop
            left join DataProp d12 on d12.objorrelobj=o.id and d12.prop=:Prop_CreatedAt
            left join DataPropVal v12 on d12.id=v12.dataprop
            left join DataProp d13 on d13.objorrelobj=o.id and d13.prop=:Prop_UpdatedAt
            left join DataPropVal v13 on d13.id=v13.dataprop
            left join DataProp d14 on d14.objorrelobj=o.id and d14.prop=:Prop_Description
            left join DataPropVal v14 on d14.id=v14.dataprop
            left join DataProp d15 on d15.objorrelobj=o.id and d15.prop=:Prop_Section
            left join DataPropVal v15 on d15.id=v15.dataprop
            left join ObjVer ov15 on ov15.ownerVer=v15.obj and ov15.lastVer=1
        where
        """+whe, map);

        //... Пересечение
        //nameObjectType
        String idsObjectType = UtDb.getWhereIds(st, "objObjectType");
        List<DbRec> stObjectType = nsiService.getObjInfo(idsObjectType, "");
        Map<Long, DbRec> mapObjectType = UtDb.getMapping(stObjectType);
        //nameObjectType
        String pvsSide = UtDb.getWhereIds(st, "pvSide");
        List<DbRec> stSide = metaService.getFactorValsInfo(pvsSide);
        Map<Long, DbRec> mapSide = UtDb.getMapping(stSide);

        for (DbRec rec : st) {
            if (mapObjectType.containsKey(rec.getLong("objObjectType"))) {
                rec.put("nameObjectType", mapObjectType.get(rec.getLong("objObjectType")).getString("name"));
            }
            if (mapSide.containsKey(rec.getLong("pvSide"))) {
                rec.put("fvSide", mapSide.get(rec.getLong("pvSide")).getString("factorVal"));
                rec.put("nameSide", mapSide.get(rec.getLong("pvSide")).getString("name"));
            }
        }
        //
        return st;
    }

    public List<DbRec> saveObjectServed(String mode, DbRec params) throws Exception {
        long own;
        UtEntityData ue = new UtEntityData(dbObject, "Obj");

        DbRec par = new DbRec(params);
        if (mode.equalsIgnoreCase("ins")) {
            // find cls(linkCls)
            long linkCls = params.getLong("linkCls");
            DbRec map = metaService.getIdFromCodOfEntity("Typ", "Typ_Object", "");
            if (map.isEmpty())
                throw new XError("NotFoundCod@Typ_Object");
            long cls = metaService.getLinkCls(linkCls, map.getLong("Typ_Object"));
            par.put("cls", cls);
            own = ue.insertEntity(par);
            //...
            params.put("own", own);
            //1 Prop_ObjectType
            if (params.getLong("objObjectType") > 0)
                fillProperties(1, "Prop_ObjectType", params);
            else
                throw new XError("[Вид объекта] не указан");
            //2 Prop_Section
            if (params.getLong("objSection") > 0)
                fillProperties(1, "Prop_Section", params);
            else
                throw new XError("[Место] не указан");
            //3 Prop_StartKm
            if (params.getLong("StartKm") > 0)
                fillProperties(1, "Prop_StartKm", params);
            else
                throw new XError("[Начало (км)] не указан");
            //4 Prop_FinishKm
            if (params.getLong("FinishKm") > 0)
                fillProperties(1, "Prop_FinishKm", params);
            else
                throw new XError("[Конец (км)] не указан");
            //5 Prop_StartPicket
            if (params.getLong("StartPicket") > 0)
                fillProperties(1, "Prop_StartPicket", params);
            //6 Prop_FinishPicket
            if (params.getLong("FinishPicket") > 0)
                fillProperties(1, "Prop_FinishPicket", params);
            //7 Prop_PeriodicityReplacement
            if (params.getLong("PeriodicityReplacement") > 0)
                fillProperties(1, "Prop_PeriodicityReplacement", params);
            //8 Prop_Side
            if (params.getLong("fvSide") > 0)
                fillProperties(1, "Prop_Side", params);
            //9 Prop_Specs
            if (!params.getString("Specs").isEmpty())
                fillProperties(1, "Prop_Specs", params);
            //10 Prop_LocationDetails
            if (!params.getString("LocationDetails").isEmpty())
                fillProperties(1, "Prop_LocationDetails", params);
            //11 Prop_Number
            if (!params.getString("Number").isEmpty())
                fillProperties(1, "Prop_Number", params);
            //12 Prop_InstallationDate
            if (!params.getString("InstallationDate").isEmpty())
                fillProperties(1, "Prop_InstallationDate", params);
            //13 Prop_CreatedAt
            if (!params.getString("CreatedAt").isEmpty())
                fillProperties(1, "Prop_CreatedAt", params);
            else
                throw new XError("[CreatedAt] не указан");
            //14 Prop_UpdatedAt
            if (!params.getString("UpdatedAt").isEmpty())
                fillProperties(1, "Prop_UpdatedAt", params);
            else
                throw new XError("[UpdatedAt] не указан");
            //15 Prop_Description
            if (!params.getString("Description").isEmpty())
                fillProperties(1, "Prop_Description", params);
            //16 Prop_User
            if (params.getLong("objUser") > 0)
                fillProperties(1, "Prop_User", params);
            else
                throw new XError("[User] не указан");
        } else if (mode.equalsIgnoreCase("upd")) {
            own = params.getLong("id");
            ue.updateEntity(par);
            //
            params.put("own", own);

            //2 Prop_Section
            if (params.containsKey("idSection")) {
                if (params.getLong("objSection") > 0)
                    updateProperties("Prop_Section", params);
                else
                    throw new XError("[Место] не указан");
            }

            //3 Prop_StartKm
            if (params.containsKey("idStartKm")) {
                if (params.getLong("StartKm") > 0)
                    updateProperties("Prop_StartKm", params);
                else
                    throw new XError("[Начало (км)] не указан");
            }

            //4 Prop_FinishKm
            if (params.containsKey("idFinishKm")) {
                if (params.getLong("FinishKm") > 0)
                    updateProperties("Prop_FinishKm", params);
                else
                    throw new XError("[Конец (км)] не указан");
            }

            //5 Prop_StartPicket
            if (params.containsKey("idStartPicket"))
                updateProperties("Prop_StartPicket", params);
            else if (params.getLong("StartPicket") > 0)
                fillProperties(1, "Prop_StartPicket", params);

            //6 Prop_FinishPicket
            if (params.containsKey("idFinishPicket"))
                updateProperties("Prop_FinishPicket", params);
            else if (params.getLong("FinishPicket") > 0)
                fillProperties(1, "Prop_FinishPicket", params);

            //7 Prop_PeriodicityReplacement
            if (params.containsKey("idPeriodicityReplacement"))
                updateProperties("Prop_PeriodicityReplacement", params);
            else if (params.getLong("PeriodicityReplacement") > 0)
                fillProperties(1, "Prop_PeriodicityReplacement", params);

            //8 Prop_Side
            if (params.containsKey("idSide"))
                updateProperties("Prop_Side", params);
            else if (params.getLong("fvSide") > 0)
                fillProperties(1, "Prop_Side", params);

            //9 Prop_Specs
            if (params.containsKey("idSpecs"))
                updateProperties("Prop_Specs", params);
            else if (!params.getString("Specs").isEmpty())
                fillProperties(1, "Prop_Specs", params);

            //10 Prop_LocationDetails
            if (params.containsKey("idLocationDetails"))
                updateProperties("Prop_LocationDetails", params);
            else if (!params.getString("LocationDetails").isEmpty())
                fillProperties(1, "Prop_LocationDetails", params);

            //11 Prop_Number
            if (params.containsKey("idNumber"))
                updateProperties("Prop_Number", params);
            else if (!params.getString("Number").isEmpty())
                fillProperties(1, "Prop_Number", params);

            //12 Prop_InstallationDate
            if (params.containsKey("idInstallationDate"))
                updateProperties("Prop_InstallationDate", params);
            else if (!params.getString("InstallationDate").isEmpty())
                fillProperties(1, "Prop_InstallationDate", params);

            //14 Prop_UpdatedAt
            if (params.containsKey("idUpdatedAt")) {
                if (!params.getString("UpdatedAt").isEmpty())
                    updateProperties("Prop_UpdatedAt", params);
                else
                    throw new XError("[UpdatedAt] не указан");
            }

            //15 Prop_Description
            if (params.containsKey("idDescription"))
                updateProperties("Prop_Description", params);
            else if (!params.getString("Description").isEmpty())
                fillProperties(1, "Prop_Description", params);

            //16 Prop_User
            if (params.containsKey("idUser")) {
                if (params.getLong("objUser") > 0)
                    updateProperties("Prop_User", params);
                else
                    throw new XError("[User] не указан");
            }
        } else {
            throw new XError("Unknown mode: " + mode);
        }
        //
        return loadObjectServed(own);
    }

    //todo Метод должен быть во всех data-сервисах
    public List<DbRec> getRefData(int isObj, long owner, String whePV) throws Exception {
        return dbObject.loadSql("""
                    select d.id from DataProp d, DataPropVal v
                    where d.id=v.dataProp and d.isObj=:isObj and v.propVal in
        """ + whePV + " and obj=:owner", Map.of("isObj", isObj, "owner", owner));
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
        UtEntityData ue = new UtEntityData(dbObject, "DataProp");
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
        List<DbRec> stDP = dbObject.loadSql("""
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
            idDP = dbObject.insertRec("DataProp", recDP);
        }
        //
        DbRec recDPV = ue.setDomain("DataPropVal", params);
        recDP.put("id", ue.getNextId("DataPropVal"));
        recDPV.put("dataProp", idDP);
        // Attrib str
        if (FD_AttribValType_consts.str == attribValType) {
            if (cod.equalsIgnoreCase("Prop_Specs") ||
                    cod.equalsIgnoreCase("Prop_LocationDetails") ||
                    cod.equalsIgnoreCase("Prop_Number")) {
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
            if (cod.equalsIgnoreCase("Prop_InstallationDate") ||
                    cod.equalsIgnoreCase("Prop_CreatedAt") ||
                    cod.equalsIgnoreCase("Prop_UpdatedAt")) {
                if (params.get(keyValue) != null || params.get(keyValue) != "") {
                    recDPV.put("dateTimeVal", LocalDate.parse(params.getString(keyValue), DateTimeFormatter.ISO_DATE));
                }
            } else throw new XError("for dev: [{0}] отсутствует в реализации", cod);
        }
        // FV
        if (FD_PropType_consts.factor == propType) {
            if (cod.equalsIgnoreCase("Prop_Side")) {    //template
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
            if (cod.equalsIgnoreCase("Prop_StartKm") ||
                    cod.equalsIgnoreCase("Prop_StartPicket") ||
                    cod.equalsIgnoreCase("Prop_FinishKm") ||
                    cod.equalsIgnoreCase("Prop_FinishPicket") ||
                    cod.equalsIgnoreCase("Prop_PeriodicityReplacement")) {
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
            if (cod.equalsIgnoreCase("Prop_ObjectType") ||
                    cod.equalsIgnoreCase("Prop_Section") ||
                    cod.equalsIgnoreCase("Prop_User")) {
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
        dbObject.insertRec("DataPropVal", recDPV);
    }

    private void updateProperties(String cod, DbRec params) throws Exception {
        //VariantMap mapProp = new VariantMap(params)
        String keyValue = cod.split("_")[1];
        long idVal = params.getLong("id" + keyValue);
        long propVal = params.getLong("pv" + keyValue);
        long objRef = params.getLong("obj" + keyValue);

        List<DbRec> stProp = metaService.getPropInfo(cod);
        //
        long propType = stProp.getFirst().getLong("propType");
        long attribValType = stProp.getFirst().getLong("attribValType");
        Integer digit = null;
        double koef = UtCnv.toDouble(stProp.getFirst().get("koef"));
        if (koef == 0) koef = 1;
        if (stProp.getFirst().get("digit") != null) digit = stProp.getFirst().getInt("digit");
        //
        DbRec recDPV = dbObject.loadRec("DataPropVal", idVal);
        String strValue = params.getString(keyValue);
        // Attrib str
        if (FD_AttribValType_consts.str == attribValType) {
            if (cod.equalsIgnoreCase("Prop_Specs") ||
                    cod.equalsIgnoreCase("Prop_LocationDetails") ||
                    cod.equalsIgnoreCase("Prop_Number")) {   //For Template
                if (!params.containsKey(keyValue) || strValue.trim().isEmpty()) {
                    dbObject.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, null);
                } else {
                    recDPV.put("strval", params.getString(keyValue));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Attrib multiStr
        if (FD_AttribValType_consts.multistr == attribValType) {
            if (cod.equalsIgnoreCase("Prop_Description")) {
                if (!params.containsKey(keyValue) || strValue.trim().isEmpty()) {
                    dbObject.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, null);
                } else {
                    recDPV.put("multistrval", params.getString(keyValue));
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        //Attrib dt
        if (FD_AttribValType_consts.dt == attribValType) {
            if (cod.equalsIgnoreCase("Prop_InstallationDate") ||
                    cod.equalsIgnoreCase("Prop_CreatedAt") ||
                    cod.equalsIgnoreCase("Prop_UpdatedAt")) {
                if (!params.containsKey(keyValue) || strValue.trim().isEmpty()) {
                    dbObject.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, null);
                } else {
                    recDPV.put("datetimeval", LocalDate.parse(params.getString(keyValue), DateTimeFormatter.ISO_DATE));
                }
            } else
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
        }

        // FV
        if (FD_PropType_consts.factor == propType) {
            if (cod.equalsIgnoreCase("Prop_Side")) {    //template
                if (propVal > 0) {
                    recDPV.put("propval", propVal);
                } else {
                    dbObject.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, null);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }

        // Measure
        if (FD_PropType_consts.measure == propType) {
            if (cod.equalsIgnoreCase("Prop_ParamsMeasure")) {
                if (propVal > 0)
                    recDPV.put("propval", propVal);
                else {
                    dbObject.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, null);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Meter
        double numberVal = 0;
        if (FD_PropType_consts.meter == propType || FD_PropType_consts.rate == propType) {
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
                    dbObject.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, null);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        // Typ
        if (FD_PropType_consts.typ == propType) {
            if (cod.equalsIgnoreCase("Prop_ObjectType") ||
                    cod.equalsIgnoreCase("Prop_Section") ||
                    cod.equalsIgnoreCase("Prop_User")) {
                if (objRef > 0) {
                    recDPV.put("propval", propVal);
                    recDPV.put("obj", objRef);
                } else {
                    dbObject.execSql("""
                        delete from DataPropVal where id=:idVal;
                        delete from DataProp where id in (
                            select id from DataProp
                            except
                            select dataProp as id from DataPropVal
                        );
                    """, null);
                }
            } else {
                throw new XError("for dev: [{0}] отсутствует в реализации", cod);
            }
        }
        long au = getUser();
        recDPV.put("authuser", au);
        recDPV.put("inputtype", FD_InputType_consts.app);
        recDPV.put("timestamp", LocalDate.now());
        dbObject.updateRec("DataPropVal", recDPV);
    }

    private long getUser() throws Exception {
        //AuthService authSvc = mdb.getApp().bean(AuthService.class);
        long au = 1; //todo authSvc.getCurrentUser().getAttrs().getLong("id");
        //if (au == 0)
        //    au = 1//throw new XError("notLogined")
        return au;
    }

    /**
     *
     * @param idsObj (id1, id2,...)
     * @param idsCls (id1, id2,...)
     * @return [{id:1, cls:1, name: 'n1', fullName: 'fn1'}, {id:2, cls:1, name: 'n2', fullName: 'fn2'}]
     * @throws Exception Exception
     */
    public List<DbRec> getObjInfo(String idsObj, String idsCls) throws Exception {
        String whe = idsCls.isEmpty() ? " o.id in "+idsObj : " o.cls in "+idsCls;
        return dbObject.loadSql("""
             select o.id, o.cls, v.name, v.fullName from Obj o, ObjVer v where o.id=v.ownerVer and
        """ + whe, null);
    }

    /**
     *
     * @param params Map, keys:
     *               1. ids - ids Owner,
     *               2. codProp - prop of ObjRef
     * @return [{id:1, cls:1, name: 'n1', fullName: 'fn1'}, {id:2, cls:1, name: 'n2', fullName: 'fn2'}]
     * @throws Exception Exception
     */
    public List<DbRec> getObjInfoFromData(DbRec params) throws Exception {
        return dbObject.loadSql("""
            select o.id, v.obj, ov.name, ov.fullName
            from Obj o
                left join DataProp d on d.objorrelobj=o.id and prop=:codProp
                left join DataPropVal v on d.id=v.dataProp
                left join ObjVer ov on v.obj=ov.ownerVer and ov.lastVer=1
            where o.id in
        """ + params.getString("ids"), params);
    }
}
