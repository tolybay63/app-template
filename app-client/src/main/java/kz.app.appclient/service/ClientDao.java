package kz.app.appclient.service;


import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtCnv;
import kz.app.appdbtools.repository.Db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientDao {
    private final Db db;


    public ClientDao(Db db) {
        this.db = db;
    }


    public List<DbRec> loadClient(long id) throws Exception {


        Map<String, Object> map = new HashMap<>();

        //Map<String, Long> map = apiMeta().get(ApiMeta).getIdFromCodOfEntity("Cls", "Cls_Client", "");
        long cls= UtCnv.toLong(map.get("Cls_Client"));
        //Store st = mdb.createStore("Obj.Client")

        String whe = "o.id=:id";
        if (id==0)
            whe = "o.cls=:Cls_Client";

        //map = apiMeta().get(ApiMeta).getIdFromCodOfEntity("Prop", "", "Prop_")
        map.put("Cls_Client", cls);

        List<DbRec> st = db.loadSql("""
                    select o.id, o.cls, v.name,
                        v1.id as idBIN, v1.strVal as BIN,
                        v2.id as idContactPerson, v2.strVal as ContactPerson,
                        v3.id as idContactDetails, v3.strVal as ContactDetails,
                        v4.id as idDescription, v4.multiStrVal as Description
                    from Obj o
                        left join ObjVer v on o.id=v.ownerVer and v.lastVer=1
                        left join DataProp d1 on d1.objorrelobj=o.id and d1.prop=:Prop_BIN
                        left join DataPropVal v1 on d1.id=v1.dataprop
                        left join DataProp d2 on d2.objorrelobj=o.id and d2.prop=:Prop_ContactPerson
                        left join DataPropVal v2 on d2.id=v2.dataprop
                        left join DataProp d3 on d3.objorrelobj=o.id and d3.prop=:Prop_ContactDetails
                        left join DataPropVal v3 on d3.id=v3.dataprop
                        left join DataProp d4 on d4.objorrelobj=o.id and d4.prop=:Prop_Description
                        left join DataPropVal v4 on d4.id=v4.dataprop
                    where
        """+whe, map);

        return st;
    }

    List<DbRec> saveClient(String mode, DbRec params) {




        return null;
    }



}
