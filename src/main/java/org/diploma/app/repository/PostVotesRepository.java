package org.diploma.app.repository;

import org.diploma.app.model.db.entity.PostVotes;
import org.diploma.app.model.db.entity.projection.PostVotesStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVotesRepository extends JpaRepository<PostVotes, Integer> {

    @Query(nativeQuery = true, value =
        "select count(if(value = 1, 1 , 0)) likesCount, count(if(value = -1, 1, 0)) dislikesCount from post_votes;"
    )
    PostVotesStatistics findAllStatistic();

    @Query(nativeQuery =  true, value =
        "select count(if(pv.value = 1, 1, 0)) likesCount, count(if(pv.value = -1, 1, 0)) dislikesCount " +
        "from post_votes pv join posts p on pv.post_id = p.id join users u on p.user_id = u.id " +
        "where u.email = ?1 and is_active = ?2 and moderation_status = ?3"
    )
    PostVotesStatistics findMyStatistic(String email, int isActive, String moderationStatus);
}
