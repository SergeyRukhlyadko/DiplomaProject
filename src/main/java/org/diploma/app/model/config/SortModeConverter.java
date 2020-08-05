package org.diploma.app.model.config;

import org.diploma.app.model.util.SortMode;
import org.springframework.core.convert.converter.Converter;

public class SortModeConverter implements Converter<String, SortMode> {

    //Обработать ConversionFailedException
    @Override
    public SortMode convert(String s) {
        return SortMode.valueOf(s.toUpperCase());
    }
}
