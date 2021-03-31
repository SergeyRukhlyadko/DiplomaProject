package org.diploma.app.controller;

import org.diploma.app.service.GlobalSettingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.diploma.app.model.db.entity.projection.GlobalSettingCodeAndValue.toMap;

@RestController
@RequestMapping("/api")
public class ApiSettingController {

    private GlobalSettingService globalSettingService;

    public ApiSettingController(GlobalSettingService globalSettingService) {
        this.globalSettingService = globalSettingService;
    }

    @GetMapping("/settings")
    Map<String, Boolean> settings() {
        return toMap(globalSettingService.getAll());
    }
}
