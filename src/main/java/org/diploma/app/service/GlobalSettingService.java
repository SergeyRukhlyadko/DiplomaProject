package org.diploma.app.service;

import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.model.db.entity.projection.GlobalSettingCodeAndValue;
import org.diploma.app.repository.GlobalSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

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
            () -> new GlobalSettingException("Global setting: " + globalSetting.name() + " not found")
        );
    }

    public Collection<GlobalSettingCodeAndValue> getAll() {
        Collection<GlobalSettingCodeAndValue> globalSettings =
            globalSettingsRepository.findBy(GlobalSettingCodeAndValue.class);

        if (!isAllGlobalSettingsExists(globalSettings)) {
            throw new GlobalSettingException("The global setting(s) missing");
        }
        return globalSettings;
    }

    private boolean isAllGlobalSettingsExists(Collection<GlobalSettingCodeAndValue> globalSettings) {
        Set<String> enumNames = Arrays.stream(GlobalSetting.values()).map(Enum::name).collect(Collectors.toSet());
        return globalSettings.stream().map(GlobalSettingCodeAndValue::getCode)
            .filter(enumNames::remove)
            .anyMatch(__ -> enumNames.isEmpty());
    }
}
