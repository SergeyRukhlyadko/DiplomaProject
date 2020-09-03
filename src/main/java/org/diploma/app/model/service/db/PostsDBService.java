package org.diploma.app.model.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.PostsCountByDate;
import org.diploma.app.model.db.entity.PostsStatistics;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.diploma.app.model.db.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

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

    public List<Integer> findAllYears() {
        return postsRepository.findAllYears();
    }

    public List<PostsCountByDate> findGroupByYear(int year) {
        return postsRepository.findByYearGroupByYear(year);
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
}
