package org.diploma.app.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

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

    public boolean exists(int id) {
        return usersRepository.existsById(id);
    }

    public boolean exists(String email) {
        return usersRepository.existsUsersByEmail(email);
    }

    public Users save(Users user) {
        return usersRepository.save(user);
    }
}
