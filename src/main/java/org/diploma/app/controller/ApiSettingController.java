package org.diploma.app.controller;

import org.diploma.app.service.GlobalSettingService;
import org.diploma.app.service.UserNotFoundException;
import org.diploma.app.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.diploma.app.model.db.entity.projection.GlobalSettingCodeAndValue.toMap;

@RestController
@RequestMapping("/api")
public class ApiSettingController {

    private GlobalSettingService globalSettingService;
    private UserService userService;

    public ApiSettingController(GlobalSettingService globalSettingService, UserService userService) {
        this.globalSettingService = globalSettingService;
        this.userService = userService;
    }

    @GetMapping("/settings")
    Map<String, Boolean> settings() {
        return toMap(globalSettingService.getAll());
    }

    @PutMapping(value = "/settings", consumes = MediaType.APPLICATION_JSON_VALUE)
    void settings(Principal principal, @RequestBody HashMap<String, Boolean> settings) {
        boolean isModerator = userService.isModerator(principal.getName()).orElseThrow(() ->
            new UserNotFoundException("Authenticated user: " + principal.getName() + " not found"));

        if (isModerator) {
            globalSettingService.update(settings);
        }
    }
}
