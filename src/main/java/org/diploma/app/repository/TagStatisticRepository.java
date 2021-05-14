package org.diploma.app.repository;

import org.diploma.app.model.db.entity.TagStatistic;
import org.diploma.app.model.db.entity.projection.TagStatisticMinMaxWeight;
import org.diploma.app.model.db.entity.projection.TagStatisticNameAndNormalizedWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagStatisticRepository extends JpaRepository<TagStatistic, Integer> {

    @Query(value = "select min(weight) as min, max(weight) as max from TagStatistic")
    Optional<TagStatisticMinMaxWeight> findMinMaxWeight();

    @Query(value = "select t.name as name, ts.normalizedWeight as normalizedWeight " +
        "from TagStatistic ts join Tags t on ts.tagId = t.id")
    List<TagStatisticNameAndNormalizedWeight> findAllNameAndNormalizedWeight();

    @Query(value = "select ts.normalizedWeight from TagStatistic ts join Tags t on ts.tagId = t.id where t.name = ?1")
    Optional<Float> findNameAndNormalizedWeightByName(String name);
}
