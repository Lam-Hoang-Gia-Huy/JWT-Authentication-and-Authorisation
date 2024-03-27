package com.example.JWTImplemenation.auth;

import com.example.JWTImplemenation.Repository.UserRepository;
import com.example.JWTImplemenation.Service.JwtService;
import com.example.JWTImplemenation.user.Role;
import com.example.JWTImplemenation.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder PasswordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponese register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(PasswordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken=jwtService.generateToken(user);
        return AuthenticationResponese.builder().token(jwtToken).build();
    }

    public AuthenticationResponese authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
                );
        var user=userRepository.findByEmail(request.getEmail()).orElseThrow();
        var token=jwtService.generateToken(user);
        return AuthenticationResponese.builder().token(token).build();
    }
}
