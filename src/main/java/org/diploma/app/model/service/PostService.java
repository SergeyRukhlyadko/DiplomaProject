package org.diploma.app.model.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.PostVotes;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Tag2post;
import org.diploma.app.model.db.entity.Tags;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.diploma.app.model.db.repository.PostsRepository;
import org.diploma.app.model.service.db.PostVotesDBService;
import org.diploma.app.model.service.db.PostsDBService;
import org.diploma.app.model.service.db.Tag2postDBService;
import org.diploma.app.model.service.db.TagsDBService;
import org.diploma.app.model.service.db.UsersDBService;
import org.diploma.app.model.util.PostStatus;
import org.diploma.app.model.util.SortMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    @Autowired
    AuthService authService;

    @Autowired
    GeneralService generalService;

    @Autowired
    PostsRepository postsRepository;

    public Page<Posts> findPosts(int offset, int limit, SortMode mode) {
        switch(mode) {
            case RECENT:
                return postsRepository.findByIsActiveAndModerationStatusAndTimeBefore(
                    PageRequest.of(offset / limit, limit, Sort.by("time").ascending()), true, ModerationStatus.ACCEPTED, LocalDateTime.now()
                );
            case POPULAR:
                return postsRepository.findByIsActiveAndModerationStatusAndTimeBeforeOrderByCommentCountDesc(
                    PageRequest.of(offset / limit, limit), true, ModerationStatus.ACCEPTED, LocalDateTime.now()
                );
            case BEST:
                return postsRepository.findByIsActiveAndModerationStatusAndTimeBeforeOrderByLikeCountDesc(
                    PageRequest.of(offset / limit, limit), true, ModerationStatus.ACCEPTED, LocalDateTime.now()
                );
            case EARLY:
                return postsRepository.findByIsActiveAndModerationStatusAndTimeBefore(
                    PageRequest.of(offset / limit, limit, Sort.by("time").descending()), true, ModerationStatus.ACCEPTED, LocalDateTime.now()
                );
            default:
                throw new IllegalArgumentException(enumValues(SortMode.class));
        }
    }

    public Page<Posts> findPosts(int offset, int limit) {
        return postsRepository.findByIsActiveAndModerationStatusAndTimeBefore(
            PageRequest.of(offset / limit, limit), true, ModerationStatus.ACCEPTED, LocalDateTime.now()
        );
    }

    public Page<Posts> findPostsByTitleOrText(int offset, int limit, String titleOrText) {
        return postsRepository.findByIsActiveAndModerationStatusAndTimeBeforeAndTitleContainingOrTextContaining(
            PageRequest.of(offset / limit, limit), true, ModerationStatus.ACCEPTED, LocalDateTime.now(), titleOrText, titleOrText
        );
    }

    public Page<Posts> findPostsByDate(int offset, int limit, LocalDate date) {
        LocalDateTime timeStart = date.atStartOfDay();
        LocalDateTime timeEnd = timeStart.plusDays(1);
        return postsRepository.findByIsActiveAndModerationStatusAndTimeGreaterThanEqualAndTimeLessThan(
            PageRequest.of(offset / limit, limit), true, ModerationStatus.ACCEPTED, timeStart, timeEnd
        );
    }

    public Page<Posts> findPostsByTag(int offset, int limit, String tagName) {
        return postsRepository.findByIsActiveAndModerationStatusAndTimeBeforeAndTagName(
            PageRequest.of(offset / limit, limit), true, ModerationStatus.ACCEPTED, LocalDateTime.now(), tagName
        );
    }

    public Page<Posts> findPostsForModeration(String email, int offset, int limit, ModerationStatus status) {
        switch(status) {
            case NEW:
                return postsRepository.findByIsActiveAndModerationStatus(PageRequest.of(offset / limit, limit), true, ModerationStatus.NEW);
            case ACCEPTED:
                return postsRepository.findByIsActiveAndModerationStatusAndModeratorEmail(
                    PageRequest.of(offset / limit, limit), true, ModerationStatus.ACCEPTED, email
                );
            case DECLINED:
                return postsRepository.findByIsActiveAndModerationStatusAndModeratorEmail(
                    PageRequest.of(offset / limit, limit), true, ModerationStatus.DECLINED, email
                );
            default:
                throw new IllegalArgumentException(enumValues(ModerationStatus.class));
        }
    }

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

            Posts post;
            if (!generalService.isEnabled(GlobalSetting.POST_PREMODERATION) && isActive) {
                Posts newPost = new Posts();
                newPost.setActive(isActive);
                newPost.setModerationStatus(ModerationStatus.ACCEPTED);
                newPost.setUserId(user);
                newPost.setTime(dateTime);
                newPost.setTitle(title);
                newPost.setText(text);
                newPost.setViewCount(0);
                post = postsDBService.save(newPost);
            } else {
                post = postsDBService.save(isActive, user, dateTime, title, text, 0);
            }

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

    public boolean editPost(String email, int postId, boolean isActive, Date timestamp, String title, String text, List<String> tags) {
        Users user = usersDBService.find(email);

        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime dateTime = LocalDateTime.ofInstant(
            timestamp.toInstant(),
            ZoneId.systemDefault()
        );

        if (dateTime.isBefore(dateTimeNow))
            dateTime = dateTimeNow;

        Posts post = postsDBService.find(postId);

        if (user.getId() == post.getUserId().getId()) {
            postsDBService.update(post, isActive, ModerationStatus.NEW, dateTime, title, text);
        } else if (user.isModerator()) {
            postsDBService.update(post, isActive, dateTime, title, text);
        } else {
            return false;
        }

        Set<Tag2post> tag2postSet = post.getTag2posts();
        if (tags.isEmpty()) {
            if (!tag2postSet.isEmpty())
                tag2postDBService.deleteAll(tag2postSet);
        } else {
            if (!tag2postSet.isEmpty()) {
                List<Tag2post> tag2postsForDeleteList = new ArrayList<>();
                for(Tag2post tag2post : tag2postSet) {
                    if (!tags.remove(tag2post.getTagId().getName()))
                        tag2postsForDeleteList.add(tag2post);
                }

                tag2postDBService.deleteAll(tag2postsForDeleteList);
            }

            for(String tagName : tags) {
                Optional<Tags> tagOptional = tagsDBService.find(tagName);
                if (tagOptional.isPresent()) {
                    tag2postDBService.save(post, tagOptional.get());
                } else {
                    Tags tag = tagsDBService.save(tagName);
                    tag2postDBService.save(post, tag);
                }
            }
        }

        return true;
    }

    public long postsCount() {
        return postsRepository.count();
    }

    public Posts find(String sessionId, int id) {
        Posts post = postsDBService.find(id);

        try {
            Users user = authService.checkAuthentication(sessionId);
            //Если авторизованный пользователь не модератор и не является автором поста, добавить счетчик просмотров
            if (!user.isModerator() && user.getId() != post.getUserId().getId()) {
                post.setViewCount(post.getViewCount() + 1);
                postsDBService.save(post);
            }
            return post;
        } catch(EntityNotFoundException | AuthenticationCredentialsNotFoundException e) {
            post.setViewCount(post.getViewCount() + 1);
            postsDBService.save(post);
            return post;
        }
    }

    public Page<Posts> findMy(String email, int offset, int limit, PostStatus postStatus) {
        Users user = usersDBService.find(email);
        int page = offset / limit;

        switch (postStatus) {
            case INACTIVE:
                return postsDBService.findInactive(page, limit, user);
            case PENDING:
                return postsDBService.findActiveAndNew(page, limit, user);
            case DECLINED:
                return postsDBService.findActiveAndDeclined(page, limit, user);
            case PUBLISHED:
                return postsDBService.findActiveAndAccepted(page, limit, user);
            default:
                StringBuilder sb = new StringBuilder();
                sb.append("Supported statuses: ");

                PostStatus[] postStatuses = PostStatus.values();
                for(int i = 0; i < postStatuses.length; i++ ) {
                    sb.append(postStatuses[i]);
                    if (i != postStatuses.length - 1)
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
                postVotesDBService.save(user, post, value);
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

    public int moderationCount() {
        return postsRepository.countByIsActiveAndModerationStatus(true, ModerationStatus.NEW);
    }

    private <E extends Enum<E>> String enumValues(Class<E> enumClass) {
        StringBuilder sb = new StringBuilder();
        sb.append("Supported statuses: ");

        E[] values = enumClass.getEnumConstants();
        for(int i = 0; i < values.length; i++ ) {
            sb.append(values[i]);
            if (i != values.length - 1)
                sb.append(", ");
        }

        return sb.toString();
    }
}
