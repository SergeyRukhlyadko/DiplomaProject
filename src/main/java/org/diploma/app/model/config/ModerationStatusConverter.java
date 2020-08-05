package org.diploma.app.model.config;

import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.springframework.core.convert.converter.Converter;

public class ModerationStatusConverter implements Converter<String, ModerationStatus> {

    @Override
    public ModerationStatus convert(String s) {
        return ModerationStatus.valueOf(s.toUpperCase());
    }
}
