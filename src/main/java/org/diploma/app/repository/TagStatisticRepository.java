package org.diploma.app.repository;

import org.diploma.app.model.db.entity.TagStatistic;
import org.diploma.app.model.db.entity.projection.TagStatisticMinMaxWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagStatisticRepository extends JpaRepository<TagStatistic, Integer> {

    @Query(value = "select min(weight) as min, max(weight) as max from TagStatistic")
    Optional<TagStatisticMinMaxWeight> findMinMaxWeight();
}
