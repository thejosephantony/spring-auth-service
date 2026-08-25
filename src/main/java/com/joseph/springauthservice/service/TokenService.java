package com.joseph.springauthservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.joseph.springauthservice.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Responsável pela geração e validação dos tokens JWT utilizados
 * na autenticação da aplicação.
 */
@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    /**
     * Gera um token JWT para o usuário autenticado.
     *
     * <p>O e-mail do usuário é utilizado como subject e o token possui
     * validade de duas horas.</p>
     *
     * @param user usuário autenticado
     * @return token JWT assinado
     */
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("spring-auth-service")
                    .withSubject(user.getEmail())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new RuntimeException(
                    "Erro ao gerar token JWT",
                    exception
            );
        }
    }

    /**
     * Valida um token JWT e retorna o e-mail armazenado no subject.
     *
     * @param token token JWT recebido na requisição
     * @return e-mail do usuário ou uma string vazia caso o token seja inválido
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("spring-auth-service")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    /**
     * Define a data de expiração do token.
     *
     * @return instante de expiração do token
     */
    private Instant genExpirationDate() {
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}