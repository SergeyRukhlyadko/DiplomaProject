package org.diploma.app.controller;

import org.diploma.app.controller.response.ResponseStatisticBody;
import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.model.db.entity.projection.PostVotesStatistics;
import org.diploma.app.model.db.entity.projection.PostsStatistics;
import org.diploma.app.service.AuthService;
import org.diploma.app.service.GeneralService;
import org.diploma.app.service.GlobalSettingService;
import org.diploma.app.service.UserNotFoundException;
import org.diploma.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiStatisticController {

    private GlobalSettingService globalSettingService;
    private AuthService authService;
    private GeneralService generalService;
    private UserService userService;

    public ApiStatisticController(
        GlobalSettingService globalSettingService, AuthService authService, GeneralService generalService, UserService userService
    ) {
        this.globalSettingService = globalSettingService;
        this.authService = authService;
        this.generalService = generalService;
        this.userService = userService;
    }

    @GetMapping("/statistics/all")
    ResponseEntity<?> statisticsAll(Principal principal) {
        if (!globalSettingService.isEnabled(GlobalSetting.STATISTICS_IS_PUBLIC)) {
            if (authService.isAuthenticated()) {
                String userName = principal.getName();
                boolean isModerator = userService.isModerator(userName).orElseThrow(() ->
                    new UserNotFoundException("Authenticated user: " + userName + " not found"));

                if (!isModerator) {
                    return ResponseEntity.status(401).build();
                }
            } else {
                return ResponseEntity.status(401).build();
            }
        }
        PostsStatistics postsStatistics = generalService.getPostStatistics();
        PostVotesStatistics postVotesStatistics = generalService.getPostVoteStatistics();

        return ResponseEntity.ok(new ResponseStatisticBody(
            postsStatistics.getPostsCount(),
            postVotesStatistics.getLikesCount(),
            postVotesStatistics.getDislikesCount(),
            postsStatistics.getViewsCount(),
            postsStatistics.getFirstPublication()
        ));
    }
}
