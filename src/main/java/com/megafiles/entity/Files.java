package com.megafiles.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.megafiles.dto.FileDTO;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;
    private String filename;
    private Long fileSize;
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Users user;

//
//    public FileDTO getFileDTO(){
//        FileDTO fileDTO=new FileDTO();
//        fileDTO.setFileId(fileId);
//        fileDTO.setFilename(filename);
//        fileDTO.setFileSize(fileSize);
//        fileDTO.setFileUrl(fileUrl);
//        return fileDTO;
//    }
}
