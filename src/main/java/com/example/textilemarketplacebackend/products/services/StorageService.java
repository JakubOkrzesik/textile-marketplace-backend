package com.example.textilemarketplacebackend.products.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.textilemarketplacebackend.global.services.EnvService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final EnvService envService;
    private final AmazonS3 s3Client;


    public String upload(MultipartFile file) throws IOException, InterruptedException {

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File must have a name");
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        if (!fileExtension.equals("png") && !fileExtension.equals("jpeg") && !fileExtension.equals("jpg")) {
            throw new IllegalArgumentException("Unsupported file type. Only PNG and JPEG are allowed.");
        }

        String key = String.format("%s.%s", UUID.randomUUID(), fileExtension);

        PutObjectRequest request = new PutObjectRequest(envService.getAWS_S3_BUCKET_NAME(), key, convertMultipartFileToFile(file));
        s3Client.putObject(request);

        int retries = 5;
        while (retries > 0) {
            try {
                s3Client.getObjectMetadata(envService.getAWS_S3_POST_PROCESS_BUCKET(), key);
                break; // Object is available
            } catch (AmazonS3Exception e) {
                if (e.getStatusCode() == 404) {
                    Thread.sleep(1000); // Wait 1 second and retry
                    retries--;
                } else {
                    throw e;
                }
            }
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s", envService.getAWS_S3_POST_PROCESS_BUCKET(), envService.getAWS_REGION(), key);
    }

    public void deleteItem(String fileName) {
        DeleteObjectRequest request = new DeleteObjectRequest(envService.getAWS_S3_POST_PROCESS_BUCKET(), fileName);
        s3Client.deleteObject(request);
    }

    public void deleteItems(String[] keys) {
        s3Client.deleteObjects(new DeleteObjectsRequest(envService.getAWS_S3_BUCKET_NAME()).withKeys(keys));
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            throw new IllegalArgumentException("Invalid file name: " + filename);
        }
        return filename.substring(lastDotIndex + 1);
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile("upload", file.getOriginalFilename());
        file.transferTo(convFile);
        return convFile;
    }
}
