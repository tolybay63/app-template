package kz.kis.kisstorage.repository;

import java.io.*;
import java.nio.file.*;

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
