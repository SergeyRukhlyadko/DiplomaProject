package org.diploma.app.service;

import org.diploma.app.model.db.entity.Users;
import org.diploma.app.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UsersRepository usersRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean exists(String email) {
        return usersRepository.existsUsersByEmail(email);
    }

    public Users save(String name, String email, String password) {
        return usersRepository.save(new Users(false, name, email, passwordEncoder.encode(password)));
    }

    public Optional<Boolean> isModerator(String email) {
        return usersRepository.isModeratorByEmail(email);
    }
}
