package org.diploma.app.model.db.repository;

import org.diploma.app.model.db.entity.PostsStatistics;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {

    int countByModerationStatus(ModerationStatus moderationStatus);

    Page<Posts> findByIsActiveAndModerationStatusAndTimeGreaterThanEqualAndTimeLessThan(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime timeGte, LocalDateTime timeLt
    );

    Page<Posts> findByIsActiveAndModerationStatusAndTimeBefore(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime time
    );

    @Query("select p from Posts p join PostComments pc on p.id = pc.postId " +
        "where p.isActive = ?1 and p.moderationStatus = ?2 and p.time < ?3 " +
        "group by p.id order by count(p) desc")
    Page<Posts> findByIsActiveAndModerationStatusAndTimeBeforeOrderByCommentCountDesc(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime time
    );

    @Query("select p from Posts p join PostVotes pv on p.id = pv.postId " +
        "where p.isActive = ?1 and p.moderationStatus = ?2 and p.time < ?3 and pv.value = 1" +
        "group by p.id order by count(p) desc")
    Page<Posts> findByIsActiveAndModerationStatusAndTimeBeforeOrderByLikeCountDesc(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime time
    );

    @Query("select p from Posts p " +
        "join Tag2post t2p on p.id = t2p.postId " +
        "join Tags t on t.id = t2p.tagId " +
        "where p.isActive = ?1 and p.moderationStatus = ?2 and p.time < ?3 and t.name = ?4")
    Page<Posts> findByIsActiveAndModerationStatusAndTimeBeforeAndTagName(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime time, String tagName
    );

    Page<Posts> findByIsActiveAndModerationStatus(Pageable pageable, boolean isActive, ModerationStatus moderationStatus);

    Page<Posts> findByIsActiveAndModerationStatusAndModeratorId(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, Users moderatorId
    );

    Page<Posts> findByIsActiveAndUserId(Pageable pageable, boolean isActive, Users userId);

    Page<Posts> findByIsActiveAndModerationStatusAndUserId(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, Users userId
    );

    @Query(nativeQuery = true, value =
        "select " +
        "count(id) postsCount, " +
        "sum(view_count) viewsCount, " +
        "unix_timestamp(min(time)) firstPublication " +
        "from posts;"
    )
    Optional<PostsStatistics> findAllStatistic();

    @Query(nativeQuery = true, value =
        "select " +
        "count(id) postsCount, " +
        "sum(view_count) viewsCount, " +
        "unix_timestamp(min(time)) firstPublication " +
        "from posts where user_id = ?1 and is_active = ?2 and moderation_status = ?3"
    )
    Optional<PostsStatistics> findMyStatistic(int userId, int isActive, String moderationStatus);

    @Query("select p from Posts p where p.isActive = ?1 and p.moderationStatus = ?2 and (p.title like %?3% or p.text like %?4%)")
    Page<Posts> findByIsActiveAndModerationStatusAndTitleContainingOrByTextContaining(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, String title, String text
    );
}
