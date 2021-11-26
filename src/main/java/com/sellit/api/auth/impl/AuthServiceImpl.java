package com.sellit.api.auth.impl;


import com.sellit.api.auth.AuthService;
import com.sellit.api.user.entity.UserEntity;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.SigninRequest;
import com.sellit.api.payload.SigninResponse;
import com.sellit.api.payload.TokenPayload;
import com.sellit.api.user.repository.UserRepository;
import com.sellit.api.security.JwtTokenProvider;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    AuthenticationManager authenticationManager;
    JwtTokenProvider tokenProvider;
    UserRepository userRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<SigninResponse> signin(SigninRequest signinRequest){
        log.info("Requesting to authenticate with the api");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signinRequest.getUserName().toLowerCase(), signinRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateJwtToken(authentication);

        log.info("Getting user information");
        UserEntity user =userRepository.findByUserName(tokenProvider.getUserNameFromToken(jwt).toLowerCase()).orElseThrow(
                ()->new EntityNotFoundException("Wrong credentials")
        );
        String serviceProviderUuid = null;
        if(user.isAProvider() && user.getProviderEntityDetails().getServices().size()>0){
            serviceProviderUuid = user.getProviderEntityDetails().getServices().get(0).getUuid();
        }
        SigninResponse response = new SigninResponse();
        TokenPayload tokenPayload = TokenPayload.builder().build();
        tokenPayload.setAccessToken(jwt);
        tokenPayload.setType("Bearer");
        tokenPayload.setExpiresIn(tokenProvider.getExpirationMinutes(jwt));
        response.setUserData(user);
        response.setTokenPayload(tokenPayload);
        response.setServiceProviderUuid(serviceProviderUuid);
        log.info("Returning user information");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> blockUserAccount(String userUuid){
        UserEntity user = userRepository.findByUuid(userUuid).orElseThrow(
                ()-> new EntityNotFoundException("No user found with the identifier provided")
        );
        user.setEnabled(false);
        userRepository.save(user);
        return new ResponseEntity<>(new ApiResponse(true, "Account disabled successfully"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> enableUser(String userUuid){
        UserEntity user = userRepository.findByUuid(userUuid).orElseThrow(
                ()-> new EntityNotFoundException("No user found with the identifier provided")
        );
        user.setEnabled(true);
        userRepository.save(user);
        return new ResponseEntity<>(new ApiResponse(true, "Account enabled"), HttpStatus.OK);
    }

}
