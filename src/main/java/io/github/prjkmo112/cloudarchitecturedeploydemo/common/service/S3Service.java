package io.github.prjkmo112.cloudarchitecturedeploydemo.common.service;

import io.awspring.cloud.s3.S3Template;
import io.github.prjkmo112.cloudarchitecturedeploydemo.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofDays(7);

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.s3.base-url}")
    private String s3BaseUrl;

    public String upload(MultipartFile file) {
        try {
            String key = "uploads/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            s3Template.upload(bucketName, key, file.getInputStream());
            return key;
        } catch (IOException e) {
            throw new ApiException("파일 업로드 실패", HttpStatus.BAD_REQUEST);
        }
    }

    public URL getDownloadUrl(String key) {
        return s3Template.createSignedGetURL(bucketName, key, PRESIGNED_URL_EXPIRATION);
    }

}
