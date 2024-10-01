package com.megafiles.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.megafiles.entity.Files;
import com.megafiles.repository.FilesRepository;
import com.megafiles.service.AzureService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AzureServiceImpl implements AzureService {
    @Value("${azure.storage.connection-string}")
    private String connectionString;

    private final FilesRepository filesRepository;

    private BlobContainerClient getBlobContainerClient(String containerName) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        return blobServiceClient.getBlobContainerClient(containerName);
    }

    // Upload a file and return the file name or URL
    public String uploadFile(MultipartFile file, String containerName) throws IOException {
        BlobContainerClient containerClient = getBlobContainerClient(containerName);
        String fileName = UUID.randomUUID() + "_" + (Objects.requireNonNull(file.getOriginalFilename()).length() > 35 ? file.getOriginalFilename().substring(0, 35) : file.getOriginalFilename());
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


    // Delete file by its ID
    public void deleteFile(Long fileId,String containerName) {
        // Retrieve file metadata from database
        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Extract file name or path from URL (stored in DB)
        String fileUrl = file.getFileUrl();
        String fileName = extractFileNameFromUrl(fileUrl);

        // Delete the file from Azure Blob Storage
        BlobContainerClient containerClient = getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        if (blobClient.exists()) {
            blobClient.delete();
        }

        // Delete file record from MySQL database
        filesRepository.delete(file);
    }

    // Helper method to extract file name from URL
    private String extractFileNameFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath();
            return path.substring(path.lastIndexOf("/") + 1);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid file URL", e);
        }
    }
}
