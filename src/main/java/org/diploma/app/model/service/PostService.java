package org.diploma.app.model.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.PostVotes;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Tags;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.diploma.app.model.service.db.PostVotesDBService;
import org.diploma.app.model.service.db.PostsDBService;
import org.diploma.app.model.service.db.Tag2postDBService;
import org.diploma.app.model.service.db.TagsDBService;
import org.diploma.app.model.service.db.UsersDBService;
import org.diploma.app.model.util.SortMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class PostService {

    @Autowired
    PostsDBService postsDBService;

    @Autowired
    UsersDBService usersDBService;

    @Autowired
    TagsDBService tagsDBService;

    @Autowired
    Tag2postDBService tag2postDBService;

    @Autowired
    PostVotesDBService postVotesDBService;

    public Map<String, String> create(String email, boolean isActive, Date timestamp, String title, String text, List<String> tags) {
        Map<String, String> errors = new HashMap<>();

        if (title.isEmpty()) {
            errors.put("title", "Заголовок не установлен");
        } else if (title.length() < 3) {
            errors.put("title", "Заголовок слишком короткий");
        }

        if (text.isEmpty()) {
            errors.put("text", "Текст публикации не установлен");
        } else if (text.length() < 50) {
            errors.put("text", "Текст публикации слишком короткий");
        }

        if (errors.isEmpty()) {
            Users user = usersDBService.find(email);

            LocalDateTime dateTimeNow = LocalDateTime.now();
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                timestamp.toInstant(),
                ZoneId.systemDefault()
            );

            if (dateTime.isBefore(dateTimeNow))
                dateTime = dateTimeNow;

            Posts post = postsDBService.save(isActive, user, dateTime, title, text, 0);

            if (!tags.isEmpty()) {
                for(String tagString : tags) {
                    Optional<Tags> tag = tagsDBService.find(tagString);
                    if (tag.isEmpty()) {
                        Tags newTag = tagsDBService.save(tagString);
                        tag2postDBService.save(post, newTag);
                    } else {
                        tag2postDBService.save(post, tag.get());
                    }

                }
            }
        }

        return  errors;
    }

    public int moderationCount(Users user) {
        if (user.isModerator())
            return postsDBService.count(ModerationStatus.NEW);

        return 0;
    }

    public Page<Posts> find(int offset, int limit, SortMode mode) {
        switch(mode) {
            case RECENT:
                return postsDBService.findActiveAndAcceptedAndBeforeNow(offset, limit, Sort.by("time").ascending());
            case EARLY:
                return postsDBService.findActiveAndAcceptedAndBeforeNow(offset, limit, Sort.by("time").descending());
            case POPULAR:
            case BEST:
        }
        return null;
    }

    public Page<Posts> find(int offset, int limit, ModerationStatus status) {
        switch(status) {
            case NEW:
                return postsDBService.findActiveAndNew(offset / limit, limit);
            case ACCEPTED:
                return postsDBService.findActiveAndAccepted(offset / limit, limit);
            case DECLINED:
                return postsDBService.findActiveAndDeclined(offset / limit, limit);
            default:
                StringBuilder sb = new StringBuilder();
                sb.append("Supported statuses: ");

                ModerationStatus[] moderationStatuses = ModerationStatus.values();
                for(int i = 0; i < moderationStatuses.length; i++ ) {
                    sb.append(moderationStatuses[i]);
                    if (i != moderationStatuses.length - 1)
                        sb.append(", ");
                }

                throw new IllegalArgumentException(sb.toString());
        }
    }

    public boolean like(String email, int postId) {
        return addPostVote(email, postId, (byte) 1);
    }

    public boolean dislike(String email, int postId) {
        return addPostVote(email, postId, (byte) -1);
    }

    private boolean addPostVote(String email, int postId, byte value) {
        try {
            Users user = usersDBService.find(email);
            Posts post = postsDBService.find(postId);
            List<PostVotes> postVotes = post.getPostVotes();
            PostVotes existVote = null;
            for(PostVotes postVote : postVotes) {
                if (postVote.getUserId().getId() == user.getId()) {
                    existVote = postVote;
                    break;
                }
            }

            if (existVote == null) {
                postVotesDBService.saveLike(user, post);
            } else if (existVote.getValue() == value) {
                return false;
            } else if (existVote.getValue() == -value) {
                existVote.setTime(LocalDateTime.now());
                existVote.setValue(value);
                postVotesDBService.save(existVote);
            }
        } catch(EntityNotFoundException e) {
            return false;
        }

        return true;
    }
}
