package com.example.textilemarketplacebackend.products.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.example.textilemarketplacebackend.global.services.EnvService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final EnvService envService;
    private final AmazonS3 s3Client;


    public String upload(MultipartFile file) {
        return null;
    }

    public void deleteItem(String fileName) {
        s3Client.deleteObject(new DeleteObjectRequest(envService.getAWS_S3_BUCKET_NAME(), fileName));
    }

    public void deleteItems(String[] keys) {
        s3Client.deleteObjects(new DeleteObjectsRequest(envService.getAWS_S3_BUCKET_NAME()).withKeys(keys));
    }
}
