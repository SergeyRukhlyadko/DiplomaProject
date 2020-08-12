package org.diploma.app.model.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.PostVotes;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.repository.PostVotesRepository;
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

    public PostVotes save(Users userId, Posts postId, byte value) {
        PostVotes postVote = new PostVotes();
        postVote.setUserId(userId);
        postVote.setPostId(postId);
        postVote.setTime(LocalDateTime.now());
        postVote.setValue(value);
        return postVotesRepository.save(postVote);
    }
}
