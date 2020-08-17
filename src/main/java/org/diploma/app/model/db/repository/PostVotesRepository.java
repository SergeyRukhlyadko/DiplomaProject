package org.diploma.app.model.db.repository;

import org.diploma.app.model.db.entity.PostVotes;
import org.diploma.app.model.db.entity.PostVotesStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVotesRepository extends JpaRepository<PostVotes, Integer> {

    @Query(nativeQuery = true, value =
        "select count(if(value = 1, 1 , null)) likesCount, count(if(value = -1, 1, null)) dislikesCount from post_votes;"
    )
    Optional<PostVotesStatistics> findAllStatistic();
}
