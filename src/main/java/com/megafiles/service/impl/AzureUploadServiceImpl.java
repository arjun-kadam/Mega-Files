package com.megafiles.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.megafiles.service.AzureUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AzureUploadServiceImpl implements AzureUploadService {
    @Value("${azure.storage.connection-string}")
    private String connectionString;

    // Dynamically get BlobContainerClient based on the provided container name
    private BlobContainerClient getBlobContainerClient(String containerName) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        return blobServiceClient.getBlobContainerClient(containerName);
    }

    // Upload a file and return the file name or URL
    public String uploadFile(MultipartFile file, String containerName) throws IOException {
        BlobContainerClient containerClient = getBlobContainerClient(containerName);
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        blobClient.upload(file.getInputStream(), file.getSize(), true);

        return fileName;  // Return file name to be used for generating the URL
    }

    // Generate a SAS token and return the full URL with the token
    public String getFileUrl(String fileName, String containerName) {
        BlobContainerClient containerClient = getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        return generateSasToken(blobClient);
    }

    // Generate a SAS token for read access
    private String generateSasToken(BlobClient blobClient) {
        BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(
                OffsetDateTime.now().plusYears(1), permission);

        String sasToken = blobClient.generateSas(values);
        return blobClient.getBlobUrl() + "?" + sasToken;
    }
}
