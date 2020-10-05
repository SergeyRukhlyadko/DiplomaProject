package org.diploma.app.config;

import org.diploma.app.util.OperatingSystemUtil;
import org.diploma.app.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${win.local-disk}")
    String localDisk;

    @Value("${upload.image-path}")
    String imagePath;

    @Autowired
    OperatingSystemUtil operatingSystemUtil;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new SortModeConverter());
        registry.addConverter(new ModerationStatusConverter());
        registry.addConverter(new PostStatusConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        switch (operatingSystemUtil.getCurrent()) {
            case WINDOWS:
                registry
                    .addResourceHandler("/" + StringUtil.trimSlash(imagePath) + "/**")
                    .addResourceLocations("file:///" + localDisk + StringUtil.trimSlash(imagePath) + "/");
                break;
            case LINUX:
                registry
                    .addResourceHandler("/" + StringUtil.trimSlash(imagePath) + "/**")
                    .addResourceLocations("file:" + StringUtil.trimSlash(imagePath) + "/");
        }
    }
}
