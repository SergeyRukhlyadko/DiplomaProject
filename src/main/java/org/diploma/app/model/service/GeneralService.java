package org.diploma.app.model.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.GlobalSettings;
import org.diploma.app.model.db.entity.Tags;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.service.db.GlobalSettingsDBService;
import org.diploma.app.model.service.db.TagsDBService;
import org.diploma.app.model.service.db.UsersDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

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
}
