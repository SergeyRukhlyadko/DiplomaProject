package org.diploma.app.model.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.GlobalSettings;
import org.diploma.app.model.db.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Iterator;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class GlobalSettingsDBService {

    @Autowired
    GlobalSettingsRepository globalSettingsRepository;

    public GlobalSettings find(String code) {
        return globalSettingsRepository.findByCode(code).orElseThrow(
            () -> new EntityNotFoundException("Global setting " + code + " not found")
        );
    }

    public Iterator<GlobalSettings> findAll() {
        Iterator<GlobalSettings> iterator = globalSettingsRepository.findAll().iterator();

        if (!iterator.hasNext())
            throw new EntityNotFoundException("Global settings not found");

        return iterator;
    }

    public void update(String code, boolean value) {
        GlobalSettings setting = globalSettingsRepository.findByCode(code).orElseThrow(
            () -> new EntityNotFoundException("Global setting " + code + " not found")
        );

        setting.setValue(value);
        globalSettingsRepository.save(setting);
    }
}
