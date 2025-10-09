package kz.app.appincident.service;

import kz.app.appcore.model.DbRec;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class IncidentDao {

    private final Db dbIncident;

    private final MetaDao metaService;

    public IncidentDao(Db dbIncident, MetaDao metaService) {
        this.dbIncident = dbIncident;
        this.metaService = metaService;
    }


    public List<DbRec> loadEvent(long id) throws Exception {
        return null;

    }

    /**
     *
     * @param owner id Obj or RelObj
     * @param isObj if isObj==1: Obj, isObj=0: RelObj
     * @throws Exception throws
     */
    private void validateForDelete(long owner, int isObj) throws Exception {
    }

    //todo Метод должен быть во всех data-сервисах
    public List<DbRec> getRefData(int isObj, long owner, String whePV) throws Exception {
        return dbIncident.loadSql("""
                    select d.id from DataProp d, DataPropVal v
                    where d.id=v.dataProp and d.isObj=:isObj and v.propVal in
        """ + whePV + " and obj=:owner", Map.of("isObj", isObj, "owner", owner));
    }

    public void deleteClientWithProps(long id) throws Exception {

    }

    public List<DbRec> saveClient(String mode, DbRec params) throws Exception {
        return null;

    }



}
