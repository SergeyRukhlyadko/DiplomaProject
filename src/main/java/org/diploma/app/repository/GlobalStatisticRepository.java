package org.diploma.app.repository;

import org.diploma.app.model.db.entity.GlobalStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalStatisticRepository extends JpaRepository<GlobalStatistic, String> {

    @Query("select value from GlobalStatistic where name = ?1")
    Optional<String> findValueByName(String name);
}
