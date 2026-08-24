package com.joseph.springauthservice.service;

import com.joseph.springauthservice.dto.CreateUserRequest;
import com.joseph.springauthservice.entity.Role;
import com.joseph.springauthservice.entity.User;
import com.joseph.springauthservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.joseph.springauthservice.exception.UserAlreadyExistsException;
import com.joseph.springauthservice.exception.UserNotFoundException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("E-mail já cadastrado");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {

        return userRepository.findById(id)
            .orElseThrow(() ->
                    new UserNotFoundException("Usuário não encontrado"));
    }

    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new UserNotFoundException("Usuário não encontrado"));
    }

    public User update(Long id, CreateUserRequest request) {
        User user = findById(id);

        if (!user.getEmail().equals(request.getEmail())
            && userRepository.existsByEmail(request.getEmail())) {

                throw new UserAlreadyExistsException("E-mail já cadastrado");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }

    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}