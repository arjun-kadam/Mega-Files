package com.megafiles.controllers;


import com.megafiles.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files/short")
@CrossOrigin("*")
public class URLShortnerController {

    private final FileService fileService;

    @GetMapping("/{randomId}")
    public ResponseEntity<String> getOriginalUrl(@PathVariable String randomId, HttpServletRequest request) {

        try{
            return ResponseEntity.status(HttpStatus.FOUND).body(fileService.getFileByShortURL(randomId,request));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something Went Wrong");
        }
    }
}

