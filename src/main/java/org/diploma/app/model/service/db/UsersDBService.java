package org.diploma.app.model.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class UsersDBService {

    @Autowired
    UsersRepository usersRepository;

    public Users find(String email) {
        return usersRepository.findByEmail(email).orElseThrow(
            () -> new EntityNotFoundException("User with email " + email + " not found")
        );
    }

    public Users findByCode(String code) {
        return usersRepository.findByCode(code).orElseThrow(
            () -> new EntityNotFoundException("User with code " + code + " not found")
        );
    }

    public boolean exists(int id) {
        return usersRepository.existsById(id);
    }

    public boolean exists(String email) {
        return usersRepository.existsUsersByEmail(email);
    }

    public Users save(boolean moderator, String name, String email, String password) {
        Users user = new Users();
        user.setModerator(moderator);
        user.setRegTime(LocalDateTime.now());
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        return usersRepository.save(user);
    }

    public Users save(Users user) {
        return usersRepository.save(user);
    }

    public Users updateCodeByEmail(String email, String code) {
        Users user = find(email);
        user.setCode(code);
        return usersRepository.save(user);
    }

    public Users updatePasswordByCode(String code, String password) {
        Users user = findByCode(code);
        user.setPassword(password);
        return usersRepository.save(user);
    }
}
