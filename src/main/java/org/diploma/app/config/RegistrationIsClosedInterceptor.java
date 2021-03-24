package org.diploma.app.config;

import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.service.GlobalSettingService;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegistrationIsClosedInterceptor implements HandlerInterceptor {

    private GlobalSettingService globalSettingService;

    public RegistrationIsClosedInterceptor(GlobalSettingService globalSettingService) {
        this.globalSettingService = globalSettingService;
    }

    /*
        @return false if global setting MULTIUSER_MODE disabled, otherwise true
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (globalSettingService.isEnabled(GlobalSetting.MULTIUSER_MODE)) {
            return true;
        }

        handleRegistrationClosed(response);
        return false;
    }

    private void handleRegistrationClosed(HttpServletResponse response) {
        response.setStatus(404);
    }
}
