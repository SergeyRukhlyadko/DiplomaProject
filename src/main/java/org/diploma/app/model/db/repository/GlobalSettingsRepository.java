package org.diploma.app.model.db.repository;

import org.diploma.app.model.db.entity.GlobalSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Integer> {

    Optional<GlobalSettings> findByCode(String code);
}
