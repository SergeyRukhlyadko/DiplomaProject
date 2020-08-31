package org.diploma.app.model.db.repository;

import org.diploma.app.model.db.entity.CaptchaCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface CaptchaCodesRepository extends JpaRepository<CaptchaCodes, Integer> {

    boolean existsByCodeAndSecretCode(String code, String secretCode);

    @Transactional
    @Modifying
    @Query("delete from CaptchaCodes where time < ?1")
    void deleteByTimeLessThen(LocalDateTime time);
}
