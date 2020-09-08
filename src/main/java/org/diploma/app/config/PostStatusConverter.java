package org.diploma.app.config;

import org.diploma.app.util.PostStatus;
import org.springframework.core.convert.converter.Converter;

public class PostStatusConverter implements Converter<String, PostStatus> {

    @Override
    public PostStatus convert(String s) {
        return PostStatus.valueOf(s.toUpperCase());
    }
}
