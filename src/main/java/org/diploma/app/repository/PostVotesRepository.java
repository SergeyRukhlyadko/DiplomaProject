package org.diploma.app.repository;

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

    @Query(nativeQuery =  true, value =
        "select count(if(pv.value = 1, 1 , null)) likesCount, count(if(pv.value = -1, 1, null)) dislikesCount " +
        "from post_votes pv join posts p on pv.post_id = p.id " +
        "where p.user_id = ?1 and is_active = ?2 and moderation_status = ?3"
    )
    Optional<PostVotesStatistics> findMyStatistic(int userId, int isActive, String moderationStatus);
}
