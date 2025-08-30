package kz.kis.kisstorage.config;

import io.minio.MinioClient;
import kz.kis.kisstorage.components.MinioStorageRepository;
import kz.kis.kisstorage.components.MinioStorageRepositoryFactory;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PageConfiguration {

    private static final Logger log = LoggerFactory.getLogger("config");

    @Value("${page.s3.content.endpoint}")
    private String contentEndpointPage;

    @Value("${page.s3.content.accessKey}")
    private String contentAccessKeyPage;

    @Value("${page.s3.content.secretKey}")
    private String contentSecretKeyPage;

    @Value("${page.s3.content.bucketName}")
    private String contentBucketNamePage;

    @Value("${page.s3.preview.endpoint}")
    private String previewEndpointPage;

    @Value("${page.s3.preview.accessKey}")
    private String previewAccessKeyPage;

    @Value("${page.s3.preview.secretKey}")
    private String previewSecretKeyPage;

    @Value("${page.s3.preview.bucketName}")
    private String previewBucketNamePage;

    @Value("${page.s3.bufferedsize}")
    private Integer bufferedSizePage;

    @Value("${page.s3.outputpath}")
    private String outputPathPage;

    @Bean(name = "pageS3")
    public MinioClient contentPageMinioClient() {
        log.info("=========================");
        log.info("PageConfiguration.contentPageMinioClient");
        log.info("contentEndpointPage: " + contentEndpointPage);
        log.info("");

        return MinioClient.builder().endpoint(contentEndpointPage).credentials(contentAccessKeyPage, contentSecretKeyPage).build();
    }

    @Bean(name = "pagePreviewS3")
    public MinioClient previewPageMinioClient() {
        log.info("=========================");
        log.info("PageConfiguration.previewPageMinioClient");
        log.info("previewEndpointPage: " + previewEndpointPage);
        log.info("");

        return MinioClient.builder().endpoint(previewEndpointPage).credentials(previewAccessKeyPage, previewSecretKeyPage).build();
    }

    @Bean(name = "pageStorage")
    public MinioStorageRepository pageStorage(@Qualifier("pageS3") MinioClient minioClient) {
        return MinioStorageRepositoryFactory.create(minioClient, contentBucketNamePage, outputPathPage, bufferedSizePage);
    }

    @Bean(name = "pagePreviewStorage")
    public MinioStorageRepository pagePreviewStorage(@Qualifier("pagePreviewS3") MinioClient minioClient) {
        return MinioStorageRepositoryFactory.create(minioClient, previewBucketNamePage, outputPathPage, bufferedSizePage);
    }

}
