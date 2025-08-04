package tech.lucas.spring_aws_s3.service;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;

@Service
public class S3Service {
    Logger log = LoggerFactory.getLogger(S3Service.class);

    private final S3Template s3Template;
    private final S3Client s3Client;

    public S3Service(S3Template s3Template, S3Client s3Client) {
        this.s3Template = s3Template;
        this.s3Client = s3Client;
    }

    public void uploadFile(String bucketName, MultipartFile file) throws IOException {
        var key = file.getOriginalFilename() != null ? file.getOriginalFilename() : "no-name";
        log.info("START - Upload do arquivo {} no bucket {}", key, bucketName);
        createBucket(bucketName);
        s3Template.upload(bucketName, key, file.getInputStream(), getObjectMetadata(file));
        log.info("Upload do arquivo '{}' realizado com sucesso no bucket '{}'", key, bucketName);
    }

    public byte[] downloadFile(String bucketName, String key) throws IOException {
        Resource resource = s3Template.download(bucketName, key);
        log.info("Arquivo '{}' recuperado com sucesso do bucket '{}'", key, bucketName);
        try (InputStream inputStream = resource.getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new IOException("Failed to read resource content", e);
        }
    }

    public URL generatePresignedUrl(String bucketName, String fileName, Long durationMinutes) {
        log.info("Gerando URL pré-assinada do arquivo {} por {} minutos", fileName, durationMinutes);
        return s3Template.createSignedGetURL(bucketName, fileName, Duration.ofMinutes(durationMinutes));
    }

    private void createBucket(String bucketName) {
        try {
            s3Client.createBucket(b -> b.bucket(bucketName));
            log.info("Bucket '{}' criado.", bucketName);
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            // Ignora se o bucket já existe
            if (e.awsErrorDetails().errorCode().equals("BucketAlreadyOwnedByYou")) {
                log.warn("O bucket já existe: {}", e.getMessage());
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private static ObjectMetadata getObjectMetadata(MultipartFile file) {
        // Criação do ObjectMetadata com o tipo de conteúdo
        return ObjectMetadata.builder()
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();
    }
}