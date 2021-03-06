package org.diploma.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.PostVotes;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Tag2post;
import org.diploma.app.model.db.entity.Tags;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.diploma.app.repository.PostsRepository;
import org.diploma.app.repository.Tag2postRepository;
import org.diploma.app.repository.TagsRepository;
import org.diploma.app.repository.UsersRepository;
import org.diploma.app.service.db.PostVotesDBService;
import org.diploma.app.service.db.PostsDBService;
import org.diploma.app.service.db.Tag2postDBService;
import org.diploma.app.service.db.TagsDBService;
import org.diploma.app.service.db.UsersDBService;
import org.diploma.app.util.DateTimeUtil;
import org.diploma.app.util.PostStatus;
import org.diploma.app.util.SortMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class PostService {

    PostsDBService postsDBService;
    UsersDBService usersDBService;
    TagsDBService tagsDBService;
    Tag2postDBService tag2postDBService;
    PostVotesDBService postVotesDBService;
    GeneralService generalService;
    UsersRepository usersRepository;
    PostsRepository postsRepository;
    TagsRepository tagsRepository;
    Tag2postRepository tag2postRepository;

    public PostService(
        PostsDBService postsDBService,
        UsersDBService usersDBService,
        TagsDBService tagsDBService,
        Tag2postDBService tag2postDBService,
        PostVotesDBService postVotesDBService,
        GeneralService generalService,
        UsersRepository usersRepository,
        PostsRepository postsRepository,
        TagsRepository tagsRepository,
        Tag2postRepository tag2postRepository
    ) {
        this.postsDBService = postsDBService;
        this.usersDBService = usersDBService;
        this.tagsDBService = tagsDBService;
        this.tag2postDBService = tag2postDBService;
        this.postVotesDBService = postVotesDBService;
        this.generalService = generalService;
        this.usersRepository = usersRepository;
        this.postsRepository = postsRepository;
        this.tagsRepository = tagsRepository;
        this.tag2postRepository = tag2postRepository;
    }

    public Page<Posts> findPosts(int offset, int limit, SortMode mode) {
        switch (mode) {
            case RECENT:
                return postsRepository.findByIsActiveAndModerationStatusAndTimeBefore(
                    PageRequest.of(offset / limit, limit, Sort.by("time").ascending()), true,
                    ModerationStatus.ACCEPTED, LocalDateTime.now()
                );
            case POPULAR:
                return postsRepository
                    .findByIsActiveAndModerationStatusAndTimeBeforeOrderByCommentCountDesc(
                        PageRequest.of(offset / limit, limit), true, ModerationStatus.ACCEPTED,
                        LocalDateTime.now()
                    );
            case BEST:
                return postsRepository
                    .findByIsActiveAndModerationStatusAndTimeBeforeOrderByLikeCountDesc(
                        PageRequest.of(offset / limit, limit), true, ModerationStatus.ACCEPTED,
                        LocalDateTime.now()
                    );
            case EARLY:
                return postsRepository.findByIsActiveAndModerationStatusAndTimeBefore(
                    PageRequest.of(offset / limit, limit, Sort.by("time").descending()), true,
                    ModerationStatus.ACCEPTED, LocalDateTime.now()
                );
            default:
                throw new IllegalArgumentException(enumValues(SortMode.class));
        }
    }

    public Page<Posts> findPosts(int offset, int limit) {
        return postsRepository.findByIsActiveAndModerationStatusAndTimeBefore(
            PageRequest.of(offset / limit, limit), true, ModerationStatus.ACCEPTED,
            LocalDateTime.now()
        );
    }

    public Page<Posts> findPostsByTitleOrText(int offset, int limit, String titleOrText) {
        return postsRepository
            .findByIsActiveAndModerationStatusAndTimeBeforeAndTitleContainingOrTextContaining(
                PageRequest.of(offset / limit, limit), true, ModerationStatus.ACCEPTED,
                LocalDateTime.now(), titleOrText, titleOrText
            );
    }

    public Page<Posts> findPostsByDate(int offset, int limit, LocalDate date) {
        LocalDateTime timeStart = date.atStartOfDay();
        LocalDateTime timeEnd = timeStart.plusDays(1);
        return postsRepository
            .findByIsActiveAndModerationStatusAndTimeGreaterThanEqualAndTimeLessThan(
                PageRequest.of(offset / limit, limit), true, ModerationStatus.ACCEPTED, timeStart,
                timeEnd
            );
    }

    public Page<Posts> findPostsByTag(int offset, int limit, String tagName) {
        return postsRepository.findByIsActiveAndModerationStatusAndTimeBeforeAndTagName(
            PageRequest.of(offset / limit, limit), true, ModerationStatus.ACCEPTED,
            LocalDateTime.now(), tagName
        );
    }

    public Page<Posts> findPostsForModeration(String email, int offset, int limit,
        ModerationStatus status) {
        switch (status) {
            case NEW:
                return postsRepository
                    .findByIsActiveAndModerationStatus(PageRequest.of(offset / limit, limit), true,
                        ModerationStatus.NEW);
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

    public Page<Posts> findMyPosts(String email, int offset, int limit, PostStatus postStatus) {
        int page = offset / limit;
        switch (postStatus) {
            case INACTIVE:
                return postsRepository.findByIsActiveAndUserEmail(PageRequest.of(page, limit), false, email);
            case PENDING:
                return postsRepository.findByIsActiveAndModerationStatusAndUserEmail(
                    PageRequest.of(page, limit), true, ModerationStatus.NEW, email
                );
            case DECLINED:
                return postsRepository.findByIsActiveAndModerationStatusAndUserEmail(
                    PageRequest.of(page, limit), true, ModerationStatus.DECLINED, email
                );
            case PUBLISHED:
                return postsRepository.findByIsActiveAndModerationStatusAndUserEmail(
                    PageRequest.of(page, limit), true, ModerationStatus.ACCEPTED, email
                );
            default:
                throw new IllegalArgumentException(enumValues(PostStatus.class));
        }
    }

    public Optional<Posts> findPostById(int id) {
        return postsRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createPost(String email, boolean isActive, Date timestamp, String title,
        String text, List<String> tags) {
        Users user = usersRepository.findByEmail(email).orElseThrow(
            () -> new UserNotFoundException("User with email " + email + " not found")
        );

        LocalDateTime dateTime = getNowIfDateTimeBefore(DateTimeUtil.toLocalDateTime(timestamp));

        Posts post;
        if (!generalService.isEnabled(GlobalSetting.POST_PREMODERATION) && isActive) {
            post = postsRepository.save(
                new Posts(isActive, ModerationStatus.ACCEPTED, user, dateTime, title, text, 0));
        } else {
            post = postsRepository.save(new Posts(isActive, user, dateTime, title, text, 0));
        }

        if (!tags.isEmpty()) {
            for (String tagString : tags) {
                Optional<Tags> tag = tagsRepository.findByName(tagString);
                if (tag.isEmpty()) {
                    Tags newTag = tagsRepository.save(new Tags(tagString));
                    tag2postRepository.save(new Tag2post(post, newTag));
                } else {
                    tag2postRepository.save(new Tag2post(post, tag.get()));
                }

            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean editPost(
        String email, int postId, boolean isActive, Date timestamp,
        String title, String text, List<String> tags
    ) {
        Users user = usersRepository.findByEmail(email).orElseThrow(
            () -> new UserNotFoundException("User with email " + email + " not found")
        );

        Posts post;
        Optional<Posts> postOptional = postsRepository.findById(postId);
        if (postOptional.isPresent()) {
            post = postOptional.get();
        } else {
            return false;
        }

        LocalDateTime dateTime = getNowIfDateTimeBefore(DateTimeUtil.toLocalDateTime(timestamp));

        if (user.getId() == post.getUser().getId()) {
            postsDBService.update(post, isActive, ModerationStatus.NEW, dateTime, title, text);
        } else if (user.isModerator()) {
            postsDBService.update(post, isActive, dateTime, title, text);
        } else {
            return false;
        }

        Set<Tag2post> tag2postSet = post.getTag2posts();
        if (tags.isEmpty()) {
            if (!tag2postSet.isEmpty()) {
                tag2postDBService.deleteAll(tag2postSet);
            }
        } else {
            if (!tag2postSet.isEmpty()) {
                List<Tag2post> tag2postsForDeleteList = new ArrayList<>();
                for (Tag2post tag2post : tag2postSet) {
                    if (!tags.remove(tag2post.getTag().getName())) {
                        tag2postsForDeleteList.add(tag2post);
                    }
                }

                tag2postDBService.deleteAll(tag2postsForDeleteList);
            }

            for (String tagName : tags) {
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
            for (PostVotes postVote : postVotes) {
                if (postVote.getUser().getId() == user.getId()) {
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
        } catch (EntityNotFoundException e) {
            return false;
        }

        return true;
    }

    public int moderationCount() {
        return postsRepository.countByIsActiveAndModerationStatus(true, ModerationStatus.NEW);
    }

    /*
        throws SQLQueryException
     */
    @Transactional(rollbackFor = Exception.class)
    public void incrementPostView(int postId) {
        if (postsRepository.incrementViewCountById(postId) != 1) {
            throw new SQLQueryException(
                "More than one row has been updated in the Posts table with id: " + postId);
        }
    }

    private <E extends Enum<E>> String enumValues(Class<E> enumClass) {
        StringBuilder sb = new StringBuilder();
        sb.append("Supported statuses: ");

        E[] values = enumClass.getEnumConstants();
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i]);
            if (i != values.length - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    private LocalDateTime getNowIfDateTimeBefore(LocalDateTime dateTime) {
        LocalDateTime dateTimeNow = LocalDateTime.now();
        return dateTime.isBefore(dateTimeNow) ? dateTimeNow : dateTime;
    }
}
