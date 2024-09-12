package com.megafiles.service.impl;

import com.megafiles.dto.*;
import com.megafiles.entity.Users;
import com.megafiles.enums.Roles;
import com.megafiles.repository.UsersRepository;
import com.megafiles.service.AuthenticationService;
import com.megafiles.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserDTO signup(SignupRequest signupRequest){
        Users user=new Users();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setRole(Roles.USER);
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setProfilePictureUrl("https://megashare.blob.core.windows.net/profiles/first-time-pfp.png");

        Users savedUser=usersRepository.save(user);
        return savedUser.getUserDto();
    }

    @Override
    public boolean hasEmailExist(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }


    public JwtAuthResponse signIn(SignInRequest signInRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(),signInRequest.getPassword()));

        }catch (BadCredentialsException e){
            throw new BadCredentialsException("Incorrect Details");
        }
        var user=usersRepository.findByEmail(signInRequest.getEmail()).orElseThrow(()->new IllegalArgumentException("Invalid Email Password"));

        var jwt=jwtService.generateToken(user);
        var refreshToken=jwtService.generateRefreshToken(new HashMap<>(),user);

        JwtAuthResponse jwtAuthResponse=new JwtAuthResponse();
        jwtAuthResponse.setToken(jwt);
        jwtAuthResponse.setRefreshToken(refreshToken);
        jwtAuthResponse.setRoles(user.getRole());
        return jwtAuthResponse;
    }

    public JwtAuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest){
        String email=jwtService.extractUserName(refreshTokenRequest.getToken());
        Users user=usersRepository.findByEmail(email).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.getToken(),user)){
            var jwt=jwtService.generateToken(user);
            JwtAuthResponse jwtAuthResponse=new JwtAuthResponse();
            jwtAuthResponse.setToken(jwt);
            jwtAuthResponse.setRefreshToken(refreshTokenRequest.getToken());
            return jwtAuthResponse;
        }
        return null;
    }
}
