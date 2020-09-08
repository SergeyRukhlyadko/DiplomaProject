package org.diploma.app.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.PostComments;
import org.diploma.app.model.db.entity.PostVotesStatistics;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.PostsCountByDate;
import org.diploma.app.model.db.entity.PostsCountByTagName;
import org.diploma.app.model.db.entity.PostsStatistics;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.diploma.app.model.db.entity.projection.GlobalSettingCodeAndValue;
import org.diploma.app.repository.GlobalSettingsRepository;
import org.diploma.app.repository.TagsRepository;
import org.diploma.app.service.db.PostCommentsDBService;
import org.diploma.app.service.db.PostVotesDBService;
import org.diploma.app.service.db.PostsDBService;
import org.diploma.app.service.db.TagsDBService;
import org.diploma.app.service.db.UsersDBService;
import org.diploma.app.util.Decision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class GeneralService {

    @Value("${upload.dir}")
    String uploadDir;

    @Autowired
    TagsDBService tagsDBService;

    @Autowired
    UsersDBService usersDBService;

    @Autowired
    PostCommentsDBService postCommentsDBService;

    @Autowired
    PostsDBService postsDBService;

    @Autowired
    PostVotesDBService postVotesDBService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TagsRepository tagsRepository;

    @Autowired
    GlobalSettingsRepository globalSettingsRepository;

    public Users changeModeratorStatus(String email) {
        Users user = usersDBService.find(email);
        user.setModerator(!user.isModerator());
        return usersDBService.save(user);
    }

    public List<PostsCountByTagName> getAllTags() {
        return tagsRepository.findAllTagNamesAndPostsCountGroupByTagName();
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

    public PostsStatistics getAllPostStatistics() {
        return postsDBService.findAllStatistic();
    }

    public PostVotesStatistics getAllPostVoteStatistics() {
        return postVotesDBService.findAllStatistic();
    }

    public PostsStatistics getMyPostStatistics(String email) {
        Users user = usersDBService.find(email);
        return postsDBService.findMyStatistic(user, true, ModerationStatus.ACCEPTED);
    }

    public PostVotesStatistics getMyPostVotesStatistics(String email) {
        Users user = usersDBService.find(email);
        return postVotesDBService.findMyStatistic(user, true, ModerationStatus.ACCEPTED);
    }

    public String uploadImage(byte[] bytes, String format) throws IOException {
        String[] hashes = UUID.randomUUID().toString().split("-");
        StringBuilder uploadPath = new StringBuilder();
        uploadPath.append("/upload/");
        for(int i = 1; i < 4; i++)
            uploadPath.append(hashes[i]).append("/");

        Files.createDirectories(Path.of(uploadDir + uploadPath.toString()));
        uploadPath.append(hashes[4]).append(".").append(format.toLowerCase());

        String uploadFile = uploadPath.toString();
        Path path = Path.of(uploadDir + uploadFile);
        Files.createFile(path);
        Files.write(path, bytes);
        return uploadFile;
    }

    public void deleteImage(Path path) throws IOException {
        Files.delete(path);
    }

    public boolean updateProfile(String email, String name, String newEmail, String password, String photo) {
        String oldPhoto = "";
        if (photo != null)
            oldPhoto = usersDBService.findPhoto(email);

        int countUpdated = usersDBService.updateUser(
            email,
            name,
            newEmail,
            password == null ? null : passwordEncoder.encode(password),
            photo
        );

        if (countUpdated == 1) {
            if (!oldPhoto.isEmpty()) {
                try {
                    deleteImage(Path.of(oldPhoto));
                } catch(IOException e) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    public List<Integer> years() {
        return postsDBService.findAllYears();
    }

    public Map<String, Integer> countByYear(int year) {
        return PostsCountByDate.toMap(postsDBService.findGroupByYear(year));
    }

    /*
        throws GlobalSettingNotFoundException
     */
    public Map<String, Boolean> findAllGlobalSettings() {
        Collection<GlobalSettingCodeAndValue> globalSettings = globalSettingsRepository.findBy(GlobalSettingCodeAndValue.class);
        if (globalSettings.isEmpty())
            throw new GlobalSettingNotFoundException("Global settings not found");

        return GlobalSettingCodeAndValue.toMap(globalSettings);
    }

    @Transactional
    public void updateGlobalSettings(Map<String, Boolean> settings) {
        List<String> trueSettings = new ArrayList<>();
        List<String> falseSettings = new ArrayList<>();
        settings.forEach((k, v) -> {
            if (v) {
                trueSettings.add(k);
            } else {
                falseSettings.add(k);
            }
        });

        if (!trueSettings.isEmpty())
            globalSettingsRepository.updateValueByCodeIn(true, trueSettings);

        if (!falseSettings.isEmpty())
            globalSettingsRepository.updateValueByCodeIn(false, falseSettings);
    }

    public boolean isEnabled(GlobalSetting globalSetting) {
        return globalSettingsRepository.valueByCode(globalSetting.toString()).orElseThrow(
            () -> new GlobalSettingNotFoundException("Global setting: " + globalSetting.toString() + " not found")
        );
    }
}
