package com.megafiles.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.megafiles.enums.FileStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    private String filename;
    private Long fileSize;

    private String fileUrl;
    private String shortUrl;

    private FileStatus fileStatus;
    private LocalDateTime uploadTime;

    private int downloadCount;
    private int reportCount;


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "uploaded_by", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Users user;

}
