package org.diploma.app.model.db.repository;

import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {

    int countByModerationStatus(ModerationStatus moderationStatus);

    Page<Posts> findByIsActiveAndModerationStatusAndTimeBefore(
        Pageable pageable, boolean isActive, ModerationStatus moderationStatus, LocalDateTime time
    );

    Page<Posts> findByIsActiveAndModerationStatus(Pageable pageable, boolean isActive, ModerationStatus moderationStatus);
}
