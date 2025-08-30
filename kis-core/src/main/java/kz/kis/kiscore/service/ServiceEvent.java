package kz.kis.kiscore.service;

public class ServiceEvent {

    /**
     * Создан новый файл (содержимого файла никогда не было в системе)
     */
    public static final String EVENT_FILE_CREATE_HASH = "file-create-hash";

    /**
     * Создан файл-дубликат (содержимое файла уже есть в системе)
     */
    public static final String EVENT_FILE_CREATE = "file-create";

    /**
     * Изменены свойства файла
     */
    public static final String EVENT_FILE_ATTRIBUTES = "file-attributes";

    //
    public static final String EVENT_FILE_MOVE = "file-move";
    public static final String EVENT_FILE_RENAME = "file-rename";
    public static final String EVENT_FILE_DELETE = "file-delete";
    public static final String EVENT_FILE_DELETE_PERMANENT = "file-delete-permanent";

    //
    public static final String EVENT_DIRECTORY_MOVE = "directory-move";

    //
    // Выполнена растеризация файла в полные страницы
    public static final String EVENT_KIS_RAST_OUT_PAGES = "EVENT_KIS_RAST_OUT_PAGES";

    // Выполнена растеризация файла для предпросмотра
    public static final String EVENT_KIS_RAST_OUT_PREVIEW = "EVENT_KIS_RAST_OUT_PREVIEW";

    // Выполнен OCR содержимого файла или страниц
    public static final String EVENT_KIS_OCR_OUT = "EVENT_KIS_OCR_OUT";

    // Выполнен парсинг содержимого файла
    public static final String EVENT_KIS_PARSER_OUT = "EVENT_KIS_PARSER_OUT";

}
