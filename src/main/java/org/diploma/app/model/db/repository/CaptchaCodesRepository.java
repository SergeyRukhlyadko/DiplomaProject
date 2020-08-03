package org.diploma.app.model.db.repository;

import org.diploma.app.model.db.entity.CaptchaCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaptchaCodesRepository extends JpaRepository<CaptchaCodes, Integer> {

    Optional<CaptchaCodes> findBySecretCode(String secretCode);
}
