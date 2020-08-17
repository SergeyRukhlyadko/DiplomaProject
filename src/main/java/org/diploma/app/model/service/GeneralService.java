package org.diploma.app.model.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.GlobalSettings;
import org.diploma.app.model.db.entity.PostComments;
import org.diploma.app.model.db.entity.PostStatistics;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Tags;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.diploma.app.model.service.db.GlobalSettingsDBService;
import org.diploma.app.model.service.db.PostCommentsDBService;
import org.diploma.app.model.service.db.PostsDBService;
import org.diploma.app.model.service.db.TagsDBService;
import org.diploma.app.model.service.db.UsersDBService;
import org.diploma.app.model.util.Decision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class GeneralService {

    @Autowired
    GlobalSettingsDBService globalSettingsDBService;

    @Autowired
    TagsDBService tagsDBService;

    @Autowired
    UsersDBService usersDBService;

    @Autowired
    PostCommentsDBService postCommentsDBService;

    @Autowired
    PostsDBService postsDBService;

    @Autowired
    AuthService authService;

    public Users changeModeratorStatus(String email) {
        Users user = usersDBService.find(email);
        user.setModerator(!user.isModerator());
        return usersDBService.save(user);
    }

    public Map<String, Boolean> getAllSettings() {
        Iterator<GlobalSettings> iterator = globalSettingsDBService.findAll();
        Map<String, Boolean> settings = new HashMap<>();

        while(iterator.hasNext()) {
            GlobalSettings gs = iterator.next();
            settings.put(gs.getCode(), gs.isValue());
        }

        return settings;
    }

    public void changeSettings(String email, Map<String, Boolean> settings) {
        Users user = usersDBService.find(email);

        if (!user.isModerator())
            throw new AccessDeniedException("User is not a moderator");

        //сделать установку настроек транзакционной
        settings.forEach((k, v) -> globalSettingsDBService.update(k, v));
    }

    public List<Tags> getAllTags() {
        return tagsDBService.finaAll();
    }

    public int addComment(String email, int parentId, int postId, String text) {
        Users user = usersDBService.find(email);
        Posts post = postsDBService.find(postId);
        if (parentId == 0) {
            return postCommentsDBService.save(post, user, text).getId();
        } else {
            PostComments postComment = postCommentsDBService.find(parentId);
            return postCommentsDBService.save(postComment, post, user, text).getId();
        }
    }

    public boolean changeModerationStatus(String email, int postId, Decision decision) {
        try {
            Users user = usersDBService.find(email);

            if (!user.isModerator())
                return false;

            Posts post = postsDBService.find(postId);
            post.setModeratorId(user);
            if (decision.name().equals(Decision.ACCEPT.toString())) {
                post.setModerationStatus(ModerationStatus.ACCEPTED);
            } else {
                post.setModerationStatus(ModerationStatus.DECLINED);
            }

            postsDBService.save(post);
        } catch(EntityNotFoundException e) {
            return false;
        }

        return true;
    }

    public PostStatistics getAllPostStatistics(String sessionId) {
        GlobalSettings globalSetting = globalSettingsDBService.find(GlobalSetting.STATISTICS_IS_PUBLIC.toString());
        if (!globalSetting.isValue()) {
            Users user = authService.checkAuthentication(sessionId);
            if (!user.isModerator())
                throw new AccessDeniedException("User is not a moderator");
        }

        return postsDBService.findAllStatistic();
    }
}
