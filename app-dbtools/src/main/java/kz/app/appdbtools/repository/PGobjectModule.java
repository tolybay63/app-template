package kz.app.appdbtools.repository;

import com.fasterxml.jackson.databind.module.*;
import org.postgresql.util.*;

public class PGobjectModule extends SimpleModule {

    public PGobjectModule() {
        this.addSerializer(PGobject.class, new PGobjectJsonSerializer());
    }

}
