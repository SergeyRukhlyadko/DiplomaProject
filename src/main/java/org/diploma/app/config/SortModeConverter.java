package org.diploma.app.config;

import org.diploma.app.util.SortMode;
import org.springframework.core.convert.converter.Converter;

public class SortModeConverter implements Converter<String, SortMode> {

    @Override
    public SortMode convert(String s) {
        return SortMode.valueOf(s.toUpperCase());
    }
}
