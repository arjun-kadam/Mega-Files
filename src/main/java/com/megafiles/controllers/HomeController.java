package com.megafiles.controllers;


import com.megafiles.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

    private final FileService fileService;

    @GetMapping()
    public ResponseEntity<?> home(){
        return ResponseEntity.ok().body("Welcome To Home");
    }

    @GetMapping("/latest-files")
    public ResponseEntity<?> latestFiles(){
        return ResponseEntity.status(HttpStatus.FOUND).body(fileService.topTenFiles());
    }

    @GetMapping("/popular-files")
    public ResponseEntity<?> popularFiles(){
        return ResponseEntity.status(HttpStatus.FOUND).body(fileService.mostPopularFiles());
    }


    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable Long fileId){

        try {
            fileService.fileDownload(fileId);
            return ResponseEntity.status(HttpStatus.OK).body("File Downloaded Success");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File Not Found");
        }

    }

    @GetMapping("/report/{fileId}")
    public ResponseEntity<?> reportFile(@PathVariable Long fileId){
        try {
            fileService.reportFile(fileId);
            return ResponseEntity.status(HttpStatus.OK).body("File Reported");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File Not Found");
        }
    }
}
