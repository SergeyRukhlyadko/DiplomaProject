package org.diploma.app.service;

import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.repository.GlobalSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class GlobalSettingService {

    private GlobalSettingsRepository globalSettingsRepository;

    public GlobalSettingService(GlobalSettingsRepository globalSettingsRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
    }

    /*
        @return false if global setting disabled, otherwise true
        @throws GlobalSettingNotFoundException if the given global setting not found in the database
     */
    public boolean isEnabled(GlobalSetting globalSetting) {
        return globalSettingsRepository.valueByCode(globalSetting.name()).orElseThrow(
            () -> new GlobalSettingNotFoundException(globalSetting)
        );
    }
}
