package kz.app.appstorage.components;

import io.minio.*;
import kz.app.appcore.utils.UtString;
import kz.app.appstorage.repository.StorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

public class MinioStorageRepository implements StorageRepository {

    private static final Logger logger = LoggerFactory.getLogger(MinioStorageRepository.class);

    private final int bufferedSize;

    private final String bucketName;

    private final String outputPath;

    private final MinioClient minioClient;

    public MinioStorageRepository(int bufferedSize, String bucketName, String outputPath, MinioClient minioClient) {
        this.bufferedSize = bufferedSize;
        this.bucketName = bucketName;
        this.outputPath = outputPath;
        this.minioClient = minioClient;
    }

    @Override
    public void uploadFile(File file, String hash) throws Exception {
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(hash)
                        .filename(file.getAbsolutePath())
                        .build()
        );
    }

    @Override
    public void deleteFile(String hash) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(hash)
                        .build()
        );
    }

    @Override
    public File downloadFile(String hash) {
        Path outputDir = Paths.get(outputPath);

        return downloadFileToDir(hash, outputDir);
    }

    @Override
    public File downloadFile(String hash, Path outputDir) throws Exception {
        return downloadFileToDir(hash, outputDir);
    }

    @Override
    public boolean checkFileExists(String hash) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(hash)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private File downloadFileToDir(String hash, Path outputDir) {
        try {
            if (Files.notExists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            // Генерация случайного guid важна для одновременной обработки файла несколькими сервисами сразу на одной машине.
            // В таком случае каждый сервис получит свою копию файла, обработает её и сам удалит.
            // В противном случае два сервиса могут конфликтовать за доступ к файлу, в частности первый сервис удалит файл
            // после себя, что помешает второму сервису выполнить обработку.
            String guid = genGuid();
            Path outputPath = outputDir.resolve(hash.replace("/", "_") + "-" + guid);
            File outputFile = outputPath.toFile();

            //
            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(hash)
                            .build())
            ) {
                byte[] buffer = new byte[bufferedSize];
                int length;
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                while ((length = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, length);
                }
                fileOutputStream.close();

                logger.info("Downloaded file {}/{} to {}", bucketName, hash, outputPath);
                return outputFile;
            }

        } catch (Exception e) {
            logger.error("Error download file {}/{} to {}, error: {}", hash, bucketName, outputPath, e.getMessage(), e);
        }

        return null;
    }

    private final SecureRandom rnd = new SecureRandom();

    private String genGuid() {
        return UtString.toHexString(rnd.nextLong());
    }

}

