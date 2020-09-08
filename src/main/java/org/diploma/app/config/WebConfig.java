package org.diploma.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${static.resource.upload}")
    String uploadResource;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new SortModeConverter());
        registry.addConverter(new ModerationStatusConverter());
        registry.addConverter(new PostStatusConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/upload/**")
            .addResourceLocations(uploadResource);
    }
}
