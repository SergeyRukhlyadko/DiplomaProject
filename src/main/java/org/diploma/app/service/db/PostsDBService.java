package org.diploma.app.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.PostsCountByDate;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.diploma.app.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Integer> findAllYears() {
        return postsRepository.findAllYears();
    }

    public List<PostsCountByDate> findGroupByYear(int year) {
        return postsRepository.findByYearGroupByYear(year);
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
