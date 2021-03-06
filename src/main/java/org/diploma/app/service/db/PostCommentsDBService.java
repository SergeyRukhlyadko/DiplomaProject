package org.diploma.app.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.PostComments;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.repository.PostCommentsRepository;
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

    public PostComments save(Posts post, Users user, String text) {
        PostComments comment = new PostComments();
        comment.setPost(post);
        comment.setUser(user);
        comment.setTime(LocalDateTime.now());
        comment.setText(text);
        return postCommentsRepository.save(comment);
    }

    public PostComments save(PostComments parent, Posts post, Users user, String text) {
        PostComments comment = new PostComments();
        comment.setParent(parent);
        comment.setPost(post);
        comment.setUser(user);
        comment.setTime(LocalDateTime.now());
        comment.setText(text);
        return postCommentsRepository.save(comment);
    }
}
