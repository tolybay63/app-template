package kz.app.appdbtools.repository;

public interface Generator {
    long genNextId(String name);

    void setId(String name);

    long getId(String name);
}
