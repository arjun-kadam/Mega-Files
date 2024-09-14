package com.megafiles.service;

import com.megafiles.dto.*;

public interface AuthenticationService {
    SignupResponse signup(SignupRequest signupRequest);
    SignInResponse signIn(SignInRequest signInRequest);
    boolean hasEmailExist(String email);
    SignInResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
