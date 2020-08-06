package org.diploma.app.model.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.diploma.app.model.db.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class PostsDBService {

    @Autowired
    PostsRepository postsRepository;

    public Posts find(int id) {
        return postsRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Post with id " + id + " not found")
        );
    }

    public Page<Posts> findActiveAndNew(int page, int size) {
        return postsRepository.findByIsActiveAndModerationStatus(
            PageRequest.of(page, size), true, ModerationStatus.NEW
        );
    }

    public Page<Posts> findActiveAndAcceptedAndModeratorId(Users user, int page, int size) {
        return postsRepository.findByIsActiveAndModerationStatusAndModeratorId(
            PageRequest.of(page, size), true, ModerationStatus.ACCEPTED, user
        );
    }

    public Page<Posts> findActiveAndDeclinedAndModeratorId(Users user, int page, int size) {
        return postsRepository.findByIsActiveAndModerationStatusAndModeratorId(
            PageRequest.of(page, size), true, ModerationStatus.DECLINED, user
        );
    }

    public Page<Posts> findActiveAndAcceptedAndBeforeNow(int offset, int limit, Sort sort) {
        return postsRepository.findByIsActiveAndModerationStatusAndTimeBefore(
            PageRequest.of(offset, limit, sort), true, ModerationStatus.ACCEPTED, LocalDateTime.now()
        );
    }

    public Posts save(boolean isActive, Users user, LocalDateTime time, String title, String text, int viewCount) {
        Posts post = new Posts();
        post.setActive(isActive);
        post.setUserId(user);
        post.setTime(time);
        post.setTitle(title);
        post.setText(text);
        post.setViewCount(viewCount);
        return postsRepository.save(post);
    }

    public int count(ModerationStatus moderationStatus) {
        return postsRepository.countByModerationStatus(moderationStatus);
    }
}
