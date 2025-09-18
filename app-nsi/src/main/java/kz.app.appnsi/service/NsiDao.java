package kz.app.appnsi.service;

import kz.app.appcore.model.DbRec;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NsiDao {
    private final Db db;
    private final MetaDao metaService;

    public NsiDao(Db db, MetaDao metaService) {
        this.db = db;
        this.metaService = metaService;
    }

    public List<DbRec> loadDefects(long obj) {


        return null;
    }

}
