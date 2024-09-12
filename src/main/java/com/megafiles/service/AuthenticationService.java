package com.megafiles.service;

import com.megafiles.dto.*;
import com.megafiles.entity.Users;

public interface AuthenticationService {
    UserDTO signup(SignupRequest signupRequest);
    JwtAuthResponse signIn(SignInRequest signInRequest);
    boolean hasEmailExist(String email);
    JwtAuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
