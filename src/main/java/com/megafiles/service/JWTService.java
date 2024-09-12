package com.megafiles.service;

import com.megafiles.entity.Users;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;


public interface JWTService {

    public boolean isTokenValid(String token, UserDetails userDetails);
    public String generateToken(UserDetails userDetails);
    public String extractUserName(String token);
    String generateRefreshToken(Map<String,Object> extractClaims, UserDetails userDetails);
}
