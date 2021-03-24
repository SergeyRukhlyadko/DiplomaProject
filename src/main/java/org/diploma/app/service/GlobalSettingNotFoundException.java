package org.diploma.app.service;

import org.diploma.app.model.db.entity.enumeration.GlobalSetting;

public class GlobalSettingNotFoundException extends RuntimeException {

    public GlobalSettingNotFoundException(String message) {
        super(message);
    }

    public GlobalSettingNotFoundException(GlobalSetting globalSetting) {
        super("Global setting: " + globalSetting.name() + " not found");
    }
}
