package org.diploma.app.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.PostVotes;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.repository.PostVotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class PostVotesDBService {

    @Autowired
    PostVotesRepository postVotesRepository;

    public PostVotes save(PostVotes postVote) {
        return postVotesRepository.save(postVote);
    }

    public PostVotes save(Users user, Posts post, byte value) {
        PostVotes postVote = new PostVotes();
        postVote.setUser(user);
        postVote.setPost(post);
        postVote.setTime(LocalDateTime.now());
        postVote.setValue(value);
        return postVotesRepository.save(postVote);
    }
}
