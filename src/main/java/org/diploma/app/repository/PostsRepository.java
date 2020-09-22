package org.diploma.app.repository;

import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.projection.PostsCountByDate;
import org.diploma.app.model.db.entity.projection.PostsStatistics;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {

    Page<Posts> findByIsActiveAndModerationStatusAndTimeBefore(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime time
    );

    @Query("select p from Posts p left join PostComments pc on p.id = pc.postId " +
        "where p.isActive = ?1 and p.moderationStatus = ?2 and p.time < ?3 " +
        "group by p.id order by count(p) desc")
    Page<Posts> findByIsActiveAndModerationStatusAndTimeBeforeOrderByCommentCountDesc(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime time
    );

    @Query("select p from Posts p left join PostVotes pv on p.id = pv.postId left join PostComments pc on p.id = pc.postId " +
        "where p.isActive = ?1 and p.moderationStatus = ?2 and p.time < ?3 " +
        "group by p.id order by count(pc.id) + count(pv.value) desc")
    Page<Posts> findByIsActiveAndModerationStatusAndTimeBeforeOrderByLikeCountDesc(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime time
    );

    @Query("select p from Posts p where p.isActive = ?1 and p.moderationStatus = ?2 and p.time < ?3 and (p.title like %?4% or p.text like %?5%)")
    Page<Posts> findByIsActiveAndModerationStatusAndTimeBeforeAndTitleContainingOrTextContaining(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime time, String title, String text
    );

    Page<Posts> findByIsActiveAndModerationStatusAndTimeGreaterThanEqualAndTimeLessThan(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime timeGte, LocalDateTime timeLt
    );

    @Query("select p from Posts p " +
        "join Tag2post t2p on p.id = t2p.postId " +
        "join Tags t on t.id = t2p.tagId " +
        "where p.isActive = ?1 and p.moderationStatus = ?2 and p.time < ?3 and t.name = ?4")
    Page<Posts> findByIsActiveAndModerationStatusAndTimeBeforeAndTagName(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime time, String tagName
    );

    Page<Posts> findByIsActiveAndModerationStatus(Pageable pageable, boolean isActive, ModerationStatus moderationStatus);

    @Query("select p from Posts p join Users u on p.moderatorId = u.id " +
        "where p.isActive = ?1 and p.moderationStatus = ?2 and u.email = ?3")
    Page<Posts> findByIsActiveAndModerationStatusAndModeratorEmail(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, String email
    );

    @Query("select p from Posts p join Users u on p.userId = u.id where p.isActive = ?1 and u.email = ?2")
    Page<Posts> findByIsActiveAndUserEmail(Pageable pageable, boolean isActive, String email);

    @Query("select p from Posts p join Users u on p.userId = u.id " +
        "where p.isActive = ?1 and p.moderationStatus = ?2 and u.email = ?3")
    Page<Posts> findByIsActiveAndModerationStatusAndUserEmail(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, String email
    );

    @Query(nativeQuery = true, value =
        "select count(id) postsCount, ifnull(sum(view_count), 0) viewsCount, ifnull(unix_timestamp(min(time)), 0) firstPublication from posts"
    )
    PostsStatistics findAllStatistic();

    @Query(nativeQuery = true, value =
        "select count(p.id) postsCount, ifnull(sum(p.view_count), 0) viewsCount, ifnull(unix_timestamp(min(p.time)), 0) firstPublication " +
        "from posts p join users u on p.user_id = u.id " +
        "where u.email = ?1 and p.is_active = ?2 and p.moderation_status = ?3"
    )
    PostsStatistics findMyStatistic(String email, int isActive, String moderationStatus);

    @Query("select year(time) as y from Posts group by y order by y")
    List<Integer> findAllYears();

    @Query("select date_format(time, '%Y-%m-%d') as date, count(id) as count from Posts where year(time) = ?1 group by year(time), month(time), day(time) order by time")
    List<PostsCountByDate> findByYearGroupByYear(int year);

    int countByIsActiveAndModerationStatus(boolean isActive, ModerationStatus moderationStatus);

    @Modifying
    @Query("update Posts set viewCount = viewCount + 1 where id = ?1")
    int incrementViewCountById(int id);
}
