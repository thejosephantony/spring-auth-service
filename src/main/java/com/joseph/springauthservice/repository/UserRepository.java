package com.joseph.springauthservice.repository;

import com.joseph.springauthservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Responsável pelo acesso e persistência de dados relacionados à entidade {@link User}.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca um usuário pelo endereço de e-mail.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se já existe um usuário cadastrado com o endereço de e-mail informado.
     */
    boolean existsByEmail(String email);
}