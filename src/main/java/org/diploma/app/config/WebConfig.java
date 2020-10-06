package org.diploma.app.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.diploma.app.util.OperatingSystemUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Configuration
public class WebConfig implements WebMvcConfigurer {

    String localDisk;
    String imagePath;
    OperatingSystemUtil operatingSystemUtil;

    public WebConfig(
        @Value("${win.local-disk}") String localDisk,
        @Value("${upload.image-path}") String imagePath,
        OperatingSystemUtil operatingSystemUtil
    ) {
        this.localDisk = localDisk;
        this.imagePath = imagePath;
        this.operatingSystemUtil = operatingSystemUtil;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new SortModeConverter());
        registry.addConverter(new ModerationStatusConverter());
        registry.addConverter(new PostStatusConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = StringUtils.strip(imagePath, "/\\");
        switch (operatingSystemUtil.getCurrent()) {
            case WINDOWS:
                registry
                    .addResourceHandler("/" + path + "/**")
                    .addResourceLocations("file:///" + localDisk + path + "/");
                break;
            case LINUX:
                registry
                    .addResourceHandler("/" + path + "/**")
                    .addResourceLocations("file:" + path + "/");
        }
    }
}
