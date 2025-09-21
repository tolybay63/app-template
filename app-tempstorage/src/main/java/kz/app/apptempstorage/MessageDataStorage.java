package kz.app.apptempstorage;

import java.util.List;

public interface MessageDataStorage {

    /**
     * Записывает данные dataList в хранилище для сущности id
     *
     * @return guid - идентификатор в хранилище
     */
    <T> String write(List<T> dataList, long id) throws Exception;

    /**
     * Читает данные по идентификатору guid
     */
    <T> List<T> read(String guid);

}