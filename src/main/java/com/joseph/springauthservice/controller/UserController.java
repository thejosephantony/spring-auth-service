package com.joseph.springauthservice.controller;

import com.joseph.springauthservice.dto.CreateUserRequest;
import com.joseph.springauthservice.dto.UserResponse;
import com.joseph.springauthservice.entity.User;
import com.joseph.springauthservice.entity.Role;
import com.joseph.springauthservice.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.joseph.springauthservice.dto.UpdateUserRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;


@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);

        User createdUser = userService.create(user);

        return UserResponse.fromEntity(createdUser);
    }

    @GetMapping
    public List<UserResponse> findAll() {

        return userService.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {

        User user = userService.findById(id);

        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateUserRequest request // <--- Usando o novo DTO de update
    ) {
        // Mapeie os dados para o UserService se necessário
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User updatedUser = userService.update(id, request); // ajuste conforme seu service

        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        userService.delete(id);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userService.findByEmail(email);

        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
        Authentication authentication,
        @Valid @RequestBody UpdateUserRequest request
    ) {
        // Pega o e-mail do usuário logado atualmente pelo token JWT
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);

        // Atualiza os dados do próprio usuário logado
        User updatedUser = userService.update(currentUser.getId(), request);

        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }
}