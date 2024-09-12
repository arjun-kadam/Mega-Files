package com.megafiles.controllers;

import com.megafiles.entity.Files;
import com.megafiles.entity.Users;
import com.megafiles.repository.FilesRepository;
import com.megafiles.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UsersRepository usersRepository;
    private final FilesRepository filesRepository;

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers(){
        return ResponseEntity.ok().body(usersRepository.findAll());
    }

    @GetMapping("/files")
    public ResponseEntity<List<Files>> getAllFiles(){
      return ResponseEntity.status(HttpStatus.OK).body(filesRepository.findAll());
    }


}
