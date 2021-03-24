package org.diploma.app.config;

import org.apache.commons.lang3.StringUtils;
import org.diploma.app.service.GlobalSettingService;
import org.diploma.app.util.OperatingSystemUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private String localDisk;
    private String imagePath;
    private OperatingSystemUtil operatingSystemUtil;
    private GlobalSettingService globalSettingService;

    public WebConfig(
        @Value("${win.local-disk}") String localDisk,
        @Value("${upload.image-path}") String imagePath,
        OperatingSystemUtil operatingSystemUtil,
        GlobalSettingService globalSettingService
    ) {
        this.localDisk = localDisk;
        this.imagePath = imagePath;
        this.operatingSystemUtil = operatingSystemUtil;
        this.globalSettingService = globalSettingService;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
            new RegistrationIsClosedInterceptor(globalSettingService)).addPathPatterns("/api/auth/register");
    }
}
