package kz.app.appdbtools.repository;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.postgresql.util.PGobject;

public class PGobjectModule extends SimpleModule {

    public PGobjectModule() {
        this.addSerializer(PGobject.class, new PGobjectJsonSerializer());
    }

}
