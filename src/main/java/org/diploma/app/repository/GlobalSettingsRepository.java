package org.diploma.app.repository;

import org.diploma.app.model.db.entity.GlobalSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Integer> {

    <T> Collection<T> findBy(Class<T> type);

    @Query("select value from GlobalSettings where code = ?1")
    Optional<Boolean> valueByCode(String code);

    @Modifying
    @Query("update GlobalSettings set value = ?1 where code in ?2")
    void updateValueByCodeIn(boolean value, Collection<String> codes);
}
