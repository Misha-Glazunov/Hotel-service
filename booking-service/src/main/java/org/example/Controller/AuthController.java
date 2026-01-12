package org.example.Controller;

import lombok.RequiredArgsConstructor;
import org.example.DTO.AuthRequest;
import org.example.DTO.AuthResponse;
import org.example.DTO.UserDTO;
import org.example.Request.UserRequest;
import org.example.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO register(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("/login")
    public AuthResponse authenticate(@RequestBody AuthRequest request) {
        return userService.authenticateUser(request);
    }
}