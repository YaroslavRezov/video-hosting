package com.example.videohosting.controller;

import com.example.specs.generated.api.AuthControllerApi;
import com.example.specs.generated.model.AuthRequestDto;
import com.example.specs.generated.model.AuthResponseDto;
import com.example.specs.generated.model.MessageResponseDto;
import com.example.videohosting.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthResponseDto> login(AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(authService.login(authRequestDto));
    }

    @Override
    public ResponseEntity<MessageResponseDto> register(AuthRequestDto authRequestDto) {
        authService.register(authRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDto().message("User registered successfully"));
    }
}
