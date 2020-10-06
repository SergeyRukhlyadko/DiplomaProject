package org.diploma.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.diploma.app.model.db.entity.PostComments;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.PostsCountByTagName;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.diploma.app.model.db.entity.projection.GlobalSettingCodeAndValue;
import org.diploma.app.model.db.entity.projection.PostVotesStatistics;
import org.diploma.app.model.db.entity.projection.PostsCountByDate;
import org.diploma.app.model.db.entity.projection.PostsStatistics;
import org.diploma.app.repository.GlobalSettingsRepository;
import org.diploma.app.repository.PostVotesRepository;
import org.diploma.app.repository.PostsRepository;
import org.diploma.app.repository.TagsRepository;
import org.diploma.app.repository.UsersRepository;
import org.diploma.app.service.db.PostCommentsDBService;
import org.diploma.app.service.db.PostsDBService;
import org.diploma.app.service.db.UsersDBService;
import org.diploma.app.util.Decision;
import org.diploma.app.util.OperatingSystemUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class GeneralService {

    String imagePath;
    UsersDBService usersDBService;
    PostCommentsDBService postCommentsDBService;
    PostsDBService postsDBService;
    PasswordEncoder passwordEncoder;
    UsersRepository usersRepository;
    PostsRepository postsRepository;
    PostVotesRepository postVotesRepository;
    TagsRepository tagsRepository;
    GlobalSettingsRepository globalSettingsRepository;
    OperatingSystemUtil operatingSystemUtil;

    public GeneralService(
        @Value("${upload.image-path}") String imagePath,
        UsersDBService usersDBService,
        PostCommentsDBService postCommentsDBService,
        PostsDBService postsDBService,
        PasswordEncoder passwordEncoder,
        UsersRepository usersRepository,
        PostsRepository postsRepository,
        PostVotesRepository postVotesRepository,
        TagsRepository tagsRepository,
        GlobalSettingsRepository globalSettingsRepository,
        OperatingSystemUtil operatingSystemUtil
    ) {
        this.imagePath = imagePath;
        this.usersDBService = usersDBService;
        this.postCommentsDBService = postCommentsDBService;
        this.postsDBService = postsDBService;
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
        this.postsRepository = postsRepository;
        this.postVotesRepository = postVotesRepository;
        this.tagsRepository = tagsRepository;
        this.globalSettingsRepository = globalSettingsRepository;
        this.operatingSystemUtil = operatingSystemUtil;
    }

    public Users changeModeratorStatus(String email) {
        Users user = usersDBService.find(email);
        user.setModerator(!user.isModerator());
        return usersDBService.save(user);
    }

    public List<PostsCountByTagName> getAllTags() {
        return tagsRepository.findAllNameAndPostsCountByIsActiveAndModerationStatusGroupByName(
            true, ModerationStatus.ACCEPTED.toString());
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

            if (!user.isModerator()) {
                return false;
            }

            Posts post = postsDBService.find(postId);
            post.setModerator(user);
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

    public PostsStatistics getPostStatistics() {
        return postsRepository.findAllStatistic(1, ModerationStatus.ACCEPTED.toString());
    }

    public PostVotesStatistics getPostVoteStatistics() {
        return postVotesRepository.findAllStatistic();
    }

    public PostsStatistics getPostStatistics(String email) {
        return postsRepository.findMyStatistic(email, 1, ModerationStatus.ACCEPTED.toString());
    }

    public PostVotesStatistics getPostVotesStatistics(String email) {
        return postVotesRepository.findMyStatistic(email, 1, ModerationStatus.ACCEPTED.toString());
    }

    public String uploadImage(byte[] bytes, String format) throws IOException {
        String[] hashes = UUID.randomUUID().toString().split("-");
        StringBuilder uploadDir = new StringBuilder();
        uploadDir.append(StringUtils.strip(imagePath, "/\\")).append("/");
        for (int i = 1; i < 4; i++) {
            uploadDir.append(hashes[i]).append("/");
        }

        operatingSystemUtil.createDirectories(uploadDir.toString());
        String uploadFile = uploadDir.append(hashes[4]).append(".").append(format.toLowerCase()).toString();
        operatingSystemUtil.createFileThenWrite(uploadFile, bytes);
        return uploadFile;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateProfile(String email, String name, String newEmail, String password,
        String photo) {
        String oldPhoto = null;
        if (photo != null) {
            oldPhoto = usersRepository.findPhoto(email);
        }

        int countUpdated = usersRepository.update(
            email,
            name,
            newEmail,
            password == null ? null : passwordEncoder.encode(password),
            photo
        );

        if (countUpdated == 1) {
            if (oldPhoto != null) {
                try {
                    operatingSystemUtil.deleteFile(oldPhoto);
                } catch (IOException e) {
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
        Collection<GlobalSettingCodeAndValue> globalSettings = globalSettingsRepository
            .findBy(GlobalSettingCodeAndValue.class);
        if (globalSettings.isEmpty()) {
            throw new GlobalSettingNotFoundException("Global settings not found");
        }

        return GlobalSettingCodeAndValue.toMap(globalSettings);
    }

    @Transactional(rollbackFor = Exception.class)
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

        if (!trueSettings.isEmpty()) {
            globalSettingsRepository.updateValueByCodeIn(true, trueSettings);
        }

        if (!falseSettings.isEmpty()) {
            globalSettingsRepository.updateValueByCodeIn(false, falseSettings);
        }
    }

    public boolean isEnabled(GlobalSetting globalSetting) {
        return globalSettingsRepository.valueByCode(globalSetting.toString()).orElseThrow(
            () -> new GlobalSettingNotFoundException(
                "Global setting: " + globalSetting.toString() + " not found")
        );
    }
}
