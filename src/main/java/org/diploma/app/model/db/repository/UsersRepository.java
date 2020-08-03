package org.diploma.app.model.db.repository;

import org.diploma.app.model.db.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByEmail(String email);

    Optional<Users> findByCode(String code);

    boolean existsUsersByEmail(String email);
}
