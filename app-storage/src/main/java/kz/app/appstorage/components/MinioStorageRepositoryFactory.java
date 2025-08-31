package kz.app.appstorage.components;

import io.minio.MinioClient;

public class MinioStorageRepositoryFactory {
    public static MinioStorageRepository create(MinioClient client, String bucketName, String outputPath, int bufferedSize) {
        return new MinioStorageRepository(bufferedSize, bucketName, outputPath, client);
    }
}