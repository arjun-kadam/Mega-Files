package com.megafiles.dto;


import lombok.Data;

@Data
public class FileDTO {
    private Long fileId;
    private String filename;
    private Long fileSize;
    private String fileUrl;
}
