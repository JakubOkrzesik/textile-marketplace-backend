package com.example.textilemarketplacebackend.global.services;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class EnvService {
    private final Dotenv dotenv = Dotenv.load();
    private final String EMAIL_SERVICE_URL = dotenv.get("EMAIL_SERVICE_ENDPOINT");
    private final String JWT_SECRET = dotenv.get("JWT_SECRET");
    private final String FRONTEND_SERVICE_URL = dotenv.get("FRONTEND_SERVICE_ENDPOINT");
    private final String IMAGE_SERVICE_URL = dotenv.get("IMAGE_UPLOAD_SERVICE_URL");
    private final String AWS_ACCESS_KEY = dotenv.get("AWS_ACCESS_KEY");
    private final String AWS_SECRET_KEY = dotenv.get("AWS_SECRET_KEY");
    private final String AWS_S3_BUCKET_NAME = dotenv.get("AWS_S3_BUCKET_NAME");
    private final String AWS_REGION = dotenv.get("AWS_REGION");
}
