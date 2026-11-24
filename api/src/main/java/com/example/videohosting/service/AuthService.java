package com.example.videohosting.service;

import com.example.specs.generated.model.AuthRequestDto;
import com.example.specs.generated.model.AuthResponseDto;
import com.example.videohosting.config.GlobalExceptionHandler;
import com.example.videohosting.exception.InvalidCredentialsException;
import com.example.videohosting.exception.UsernameAlreadyExistsException;
import com.example.videohosting.model.entity.UserEntity;
import com.example.videohosting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.registration-secret}")
    private String registrationSecret;

    public AuthResponseDto login(AuthRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }

        UserDetails user = userService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(user);

        return new AuthResponseDto()
                .token(token)
                .username(user.getUsername());
    }

    @Transactional
    public void register(AuthRequestDto request) {
        if (request.getSecret() == null || !request.getSecret().equals(registrationSecret)) {
            throw new RuntimeException("Invalid registration secret"); // You can make a custom exception for this later
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        UserEntity newUser = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(newUser);
    }
}