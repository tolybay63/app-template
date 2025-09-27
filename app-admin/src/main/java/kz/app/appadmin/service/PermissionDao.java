package kz.app.appadmin.service;

import kz.app.appdbtools.repository.Db;
import org.springframework.stereotype.Component;


@Component
public class PermissionDao {
    private final Db dbAdmin;


    public PermissionDao(Db dbAdmin) {
        this.dbAdmin = dbAdmin;
    }





}
