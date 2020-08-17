package org.diploma.app.model.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.PostsStatistics;
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
import java.time.LocalDate;
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

    public Page<Posts> findActiveAndAcceptedAndEqualsDate(int page, int size, LocalDate date) {
        LocalDateTime timeStart = date.atStartOfDay();
        LocalDateTime timeEnd = timeStart.plusDays(1);
        return postsRepository.findByIsActiveAndModerationStatusAndTimeGreaterThanEqualAndTimeLessThan(
            PageRequest.of(page, size), true, ModerationStatus.ACCEPTED, timeStart, timeEnd
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

    public Page<Posts> findActiveAndAcceptedAndBeforeNow(int page, int limit, Sort sort) {
        return postsRepository.findByIsActiveAndModerationStatusAndTimeBefore(
            PageRequest.of(page, limit, sort), true, ModerationStatus.ACCEPTED, LocalDateTime.now()
        );
    }

    public Page<Posts> findActiveAndAcceptedAndBeforeNowOrderByCommentCountDesc(int page, int limit) {
        return postsRepository.findByIsActiveAndModerationStatusAndTimeBeforeOrderByCommentCountDesc(
            PageRequest.of(page, limit), true, ModerationStatus.ACCEPTED, LocalDateTime.now()
        );
    }

    public Page<Posts> findActiveAndAcceptedAndBeforeNowOrderByLikeCountDesc(int page, int limit) {
        return postsRepository.findByIsActiveAndModerationStatusAndTimeBeforeOrderByLikeCountDesc(
            PageRequest.of(page, limit), true, ModerationStatus.ACCEPTED, LocalDateTime.now()
        );
    }

    public Page<Posts> findActiveAndAcceptedAndBeforeNow(int page, int limit, String tagName) {
        return postsRepository.findByIsActiveAndModerationStatusAndTimeBeforeAndTagName(
            PageRequest.of(page, limit), true, ModerationStatus.ACCEPTED, LocalDateTime.now(), tagName
        );
    }

    public Page<Posts> findInactive(int page, int limit, Users user) {
        return postsRepository.findByIsActiveAndUserId(PageRequest.of(page, limit), false, user);
    }

    public Page<Posts> findActiveAndNew(int page, int limit, Users user) {
        return postsRepository.findByIsActiveAndModerationStatusAndUserId(
            PageRequest.of(page, limit), true, ModerationStatus.NEW, user
        );
    }

    public Page<Posts> findActiveAndDeclined(int page, int limit, Users user) {
        return postsRepository.findByIsActiveAndModerationStatusAndUserId(
            PageRequest.of(page, limit), true, ModerationStatus.DECLINED, user
        );
    }

    public Page<Posts> findActiveAndAccepted(int page, int limit, Users user) {
        return postsRepository.findByIsActiveAndModerationStatusAndUserId(
            PageRequest.of(page, limit), true, ModerationStatus.ACCEPTED, user
        );
    }

    public PostsStatistics findAllStatistic() {
        //Протестировать запрос, при отсутствии данных и написать обработку
        return postsRepository.findAllStatistic().get();
    }

    public PostsStatistics findMyStatistic(Users user, boolean isActive, ModerationStatus moderationStatus) {
        return postsRepository.findMyStatistic(user.getId(), isActive ? 1 : 0, moderationStatus.toString()).get();
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

    public Posts save(Posts post) {
        return postsRepository.save(post);
    }

    public Posts update(Posts post, boolean isActive, LocalDateTime time, String title, String text) {
        post.setActive(isActive);
        post.setTime(time);
        post.setTitle(title);
        post.setText(text);
        return postsRepository.save(post);
    }

    public Posts update(Posts post, boolean isActive, ModerationStatus moderationStatus, LocalDateTime time, String title, String text) {
        post.setActive(isActive);
        post.setModerationStatus(moderationStatus);
        post.setTime(time);
        post.setTitle(title);
        post.setText(text);
        return postsRepository.save(post);
    }

    public int count(ModerationStatus moderationStatus) {
        return postsRepository.countByModerationStatus(moderationStatus);
    }
}
