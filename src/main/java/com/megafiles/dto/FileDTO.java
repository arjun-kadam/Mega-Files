package com.megafiles.dto;


import com.megafiles.enums.FileStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileDTO {
    private Long fileId;
    private String filename;
    private Long fileSize;
    private String fileUrl;
    private String shortUrl;
    private FileStatus fileStatus;
    private LocalDateTime uploadTime;
    private int downloadCount;
    private int reportCount;
    private String username;
    private String profilePictureUrl;
}
