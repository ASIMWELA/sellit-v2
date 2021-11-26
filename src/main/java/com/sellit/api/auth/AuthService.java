package com.sellit.api.auth;

import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.SigninRequest;
import com.sellit.api.payload.SigninResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<SigninResponse> signin(SigninRequest signinRequest);
    ResponseEntity<ApiResponse> blockUserAccount(String userUuid);
    ResponseEntity<ApiResponse> enableUser(String userUuid);
}
