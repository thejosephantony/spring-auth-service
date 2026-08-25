package com.joseph.springauthservice.controller;

import com.joseph.springauthservice.dto.LoginRequest;
import com.joseph.springauthservice.dto.LoginResponse;
import com.joseph.springauthservice.entity.User;
import com.joseph.springauthservice.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pela autenticação dos usuários.
 *
 * <p>Recebe as credenciais de login, delega a autenticação ao
 * Spring Security e, após a autenticação bem-sucedida, gera um
 * token JWT para acesso aos endpoints protegidos.</p>
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(
            AuthenticationManager authenticationManager,
            TokenService tokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    /**
     * Autentica um usuário e retorna um token JWT.
     *
     * @param request credenciais de acesso
     * @return resposta contendo o token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                );

        Authentication authentication =
                authenticationManager.authenticate(authenticationToken);

        User user = (User) authentication.getPrincipal();

        String token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}