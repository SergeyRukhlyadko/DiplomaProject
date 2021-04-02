package org.diploma.app.repository;

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

    <T> Optional<T> findByEmail(String email, Class<T> projection);

    boolean existsUsersByEmail(String email);

    @Query("select isModerator from Users where email = ?1")
    Optional<Boolean> isModeratorByEmail(String email);

    @Modifying
    @Query("update Users set code = ?1 where email = ?2")
    int updateCodeByEmail(String code, String email);

    @Modifying
    @Query("update Users set password = ?1 where code = ?2")
    int updatePasswordByCode(String password, String code);

    @Modifying
    @Query(nativeQuery = true, value = "update users u set " +
        "u.name = if (:name is null, u.name, :name), " +
        "u.email = if (:newEmail is null, u.email , :newEmail), " +
        "u.password = if (:password is null, u.password, :password), " +
        "u.photo = if (:photo is null, u.photo, :photo)" +
        "where u.email = :email"
    )
    int updateByEmail(@Param("email") String email,
                      @Param("name") String name,
                      @Param("newEmail") String newEmail,
                      @Param("password") String password,
                      @Param("photo") String photo);

    @Query("select photo from Users where email = ?1")
    String findPhoto(String email);
}
