package org.diploma.app.repository;

import org.diploma.app.model.db.entity.GlobalStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalStatisticRepository extends JpaRepository<GlobalStatistic, String> {}
