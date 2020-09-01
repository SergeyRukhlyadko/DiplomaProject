package org.diploma.app.model.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public int updateUser(String email, String name, String newEmail, String password, String photo) {
        return usersRepository.update(email, name, newEmail, password, photo);
    }

    public String findPhoto(String email) {
        return usersRepository.findPhoto(email).orElseThrow(
            () -> new EntityNotFoundException("Photo with email " + email + " not found")
        );
    }
}
