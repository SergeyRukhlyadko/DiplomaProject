package org.diploma.app.model.db.repository;

import org.diploma.app.model.db.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByEmail(String email);

    Optional<Users> findByCode(String code);

    boolean existsUsersByEmail(String email);

    @Query("select isModerator from Users where email = ?1")
    Optional<Boolean> isModeratorByEmail(String email);

    @Modifying
    @Query("update Users set code = ?1 where email = ?2")
    int updateCodeByEmail(String code, String email);

    @Modifying
    @Query(nativeQuery = true, value = "update users u set " +
        "u.name = if (:name is null, u.name, :name), " +
        "u.email = if (:newEmail is null, u.email , :newEmail), " +
        "u.password = if (:password is null, u.password, :password), " +
        "u.photo = if (:photo is null, u.photo, :photo)" +
        "where u.email = :email"
    )
    int update(@Param("email") String email,
                   @Param("name") String name,
                   @Param("newEmail") String newEmail,
                   @Param("password") String password,
                   @Param("photo") String photo);

    @Query("select u.photo from Users u where u.email = ?1")
    Optional<String> findPhoto(String email);
}
