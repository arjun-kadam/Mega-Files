package com.megafiles.service.impl;

import com.megafiles.dto.*;
import com.megafiles.entity.Users;
import com.megafiles.enums.Roles;
import com.megafiles.enums.UserStatus;
import com.megafiles.repository.UsersRepository;
import com.megafiles.service.AuthenticationService;
import com.megafiles.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public SignupResponse signup(SignupRequest signupRequest){
        Users user=new Users();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setRole(Roles.USER);
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setProfilePictureUrl("https://megashare.blob.core.windows.net/profiles/first-time-pfp.png");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setRegisterDate(LocalDateTime.now());
        user.setLastProfileUpdate(LocalDateTime.now().minusDays(8));
        Users savedUser=usersRepository.save(user);
        return new SignupResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getProfilePictureUrl(),
                savedUser.getUserStatus().toString(),
                savedUser.getRole().toString(),
                savedUser.getRegisterDate(),
                savedUser.getLastProfileUpdate()
        );
    }

    @Override
    public boolean hasEmailExist(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }


    public SignInResponse signIn(SignInRequest signInRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(),signInRequest.getPassword()));

        }catch (BadCredentialsException e){
            throw new BadCredentialsException("Incorrect Details");
        }
        var user=usersRepository.findByEmail(signInRequest.getEmail()).orElseThrow(()->new IllegalArgumentException("Invalid Email Password"));

        var jwt=jwtService.generateToken(user);
        var refreshToken=jwtService.generateRefreshToken(new HashMap<>(),user);

        SignInResponse signInResponse =new SignInResponse();
        signInResponse.setToken(jwt);
        signInResponse.setRefreshToken(refreshToken);
        signInResponse.setRoles(user.getRole());
        signInResponse.setStatus(user.getUserStatus());
        signInResponse.setProfilePictureUrl(user.getProfilePictureUrl());
        return signInResponse;
    }

    public SignInResponse refreshToken(RefreshTokenRequest refreshTokenRequest){
        String email=jwtService.extractUserName(refreshTokenRequest.getToken());
        Users user=usersRepository.findByEmail(email).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.getToken(),user)){
            var jwt=jwtService.generateToken(user);
            SignInResponse signInResponse =new SignInResponse();
            signInResponse.setToken(jwt);
            signInResponse.setRefreshToken(refreshTokenRequest.getToken());
            signInResponse.setStatus(user.getUserStatus());
            return signInResponse;

        }
        return null;
    }
}
