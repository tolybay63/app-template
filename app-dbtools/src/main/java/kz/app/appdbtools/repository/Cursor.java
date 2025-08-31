package kz.app.appdbtools.repository;

import java.util.Map;

public interface Cursor {
    boolean next() throws Exception;

    boolean eof() throws Exception;

    Map getRecord() throws Exception;

    void close();
}
