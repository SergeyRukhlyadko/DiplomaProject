package org.diploma.app.model.db.repository;

import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {

    int countByModerationStatus(ModerationStatus moderationStatus);

    Page<Posts> findByIsActiveAndModerationStatusAndTimeBefore(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime time
    );

    @Query("select p from Posts p \n" +
        "join Tag2post t2p on p.id = t2p.postId \n" +
        "join Tags t on t.id = t2p.tagId \n" +
        "where p.isActive = ?1 and p.moderationStatus = ?2 and p.time < ?3 and t.name = ?4")
    Page<Posts> findByIsActiveAndModerationStatusAndTimeBeforeAndTagName(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime time, String tagName
    );

    Page<Posts> findByIsActiveAndModerationStatus(Pageable pageable, boolean isActive, ModerationStatus moderationStatus);

    Page<Posts> findByIsActiveAndModerationStatusAndModeratorId(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, Users moderatorId
    );
}
