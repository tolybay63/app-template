package kz.app.appstorage.repository;

import java.io.File;
import java.nio.file.Path;

/**
 * Работа с хранилищем контента (файлы по хэшу, например Minio)
 */
public interface StorageRepository {

    void uploadFile(File file, String hash) throws Exception;

    void deleteFile(String hash) throws Exception;

    File downloadFile(String hash);

    File downloadFile(String hash, Path outputDir) throws Exception;

    boolean checkFileExists(String hash);

}
