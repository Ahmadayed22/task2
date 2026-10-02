package com.task2.portal.service;



import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.task2.portal.dto.LoginRequest;
import com.task2.portal.dto.LoginResponse;
import com.task2.portal.dto.SignupRequest;
import com.task2.portal.entity.Customer;
import com.task2.portal.enums.Role;
import com.task2.portal.entity.User;
import com.task2.portal.repo.CustomerRepo;
import com.task2.portal.repo.UserRepo;
import com.task2.portal.security.AuthenticatedUser;
import com.task2.portal.security.JwtService;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final CustomerRepo customerRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepo userRepo,
            CustomerRepo customerRepo,
            PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.customerRepo = customerRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void signup(SignupRequest request) {

        String username = request.username().trim();
        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepo.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username already exists"
            );
        }

        if (customerRepo.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        Customer customer = customerRepo.save(
                new Customer(
                        request.name().trim(),
                        email,
                        request.phone().trim()
                )
        );

        User user = new User(
                username,
                passwordEncoder.encode(request.password()),
                Role.CUSTOMER,
                customer.getId()
        );

        userRepo.save(user);
    }

    public LoginResponse login(LoginRequest request) {

        var authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

        AuthenticatedUser user =
                (AuthenticatedUser) authentication.getPrincipal();

        return new LoginResponse(
                jwtService.generateToken(user),
                jwtService.expirationSeconds()
        );
    }
}


