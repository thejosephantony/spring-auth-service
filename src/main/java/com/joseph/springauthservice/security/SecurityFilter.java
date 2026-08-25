package com.joseph.springauthservice.security;

import com.joseph.springauthservice.entity.User;
import com.joseph.springauthservice.repository.UserRepository;
import com.joseph.springauthservice.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro responsável por autenticar requisições utilizando tokens JWT.
 *
 * <p>O token é extraído do cabeçalho Authorization, validado pelo
 * {@link TokenService} e utilizado para localizar o usuário autenticado.
 * Após a validação, a autenticação é registrada no contexto de segurança
 * do Spring Security.</p>
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public SecurityFilter(
            TokenService tokenService,
            UserRepository userRepository
    ) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = recoverToken(request);

        if (token != null) {
            String email = tokenService.validateToken(token);

            if (!email.isEmpty()) {
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("Usuário não encontrado"));

                var authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7);
    }
}