package kz.app.appstorage.config;

import io.minio.MinioClient;
import kz.app.appstorage.components.MinioStorageRepository;
import kz.app.appstorage.components.MinioStorageRepositoryFactory;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfiguration {

    private static final Logger log = LoggerFactory.getLogger("config");

    @Value("${storage.s3.content.endpoint}")
    private String contentEndpoint;

    @Value("${storage.s3.content.accessKey}")
    private String contentAccessKey;

    @Value("${storage.s3.content.secretKey}")
    private String contentSecretKey;

    @Value("${storage.s3.content.bucketName}")
    private String contentBucketName;

    @Value("${storage.s3.bufferedsize}")
    private int bufferedSize;

    @Value("${storage.s3.outputpath}")
    private String outputPath;

    @Bean(name = "contentS3")
    public MinioClient contentMinioClient() {
        log.info("=========================");
        log.info("StorageConfiguration.contentMinioClient");
        log.info("contentEndpoint: " + contentEndpoint);
        log.info("");

        return MinioClient.builder().endpoint(contentEndpoint).credentials(contentAccessKey, contentSecretKey).build();
    }

    @Bean(name = "contentStorage")
    public MinioStorageRepository contentStorageRepository(@Qualifier("contentS3") MinioClient minioClient) {
        return MinioStorageRepositoryFactory.create(minioClient, contentBucketName, outputPath, bufferedSize);
    }

}