package org.diploma.app.model.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.PostComments;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.repository.PostCommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class PostCommentsDBService {

    @Autowired
    PostCommentsRepository postCommentsRepository;

    public PostComments find(int id) {
        return postCommentsRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("PostComment with id " + id + " not found")
        );
    }

    public PostComments save(Posts postId, Users userId, String text) {
        PostComments comment = new PostComments();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setTime(LocalDateTime.now());
        comment.setText(text);
        return postCommentsRepository.save(comment);
    }

    public PostComments save(PostComments parentId, Posts postId, Users userId, String text) {
        PostComments comment = new PostComments();
        comment.setParentId(parentId);
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setTime(LocalDateTime.now());
        comment.setText(text);
        return postCommentsRepository.save(comment);
    }
}
