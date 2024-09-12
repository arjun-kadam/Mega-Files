package com.megafiles.controllers;

import com.megafiles.entity.Users;
import com.megafiles.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UsersRepository usersRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Users>> getAllUsers(){
        return ResponseEntity.ok().body(usersRepository.findAll());
    }
}
