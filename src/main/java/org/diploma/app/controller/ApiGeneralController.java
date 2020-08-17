package org.diploma.app.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.request.post.RequestCommentBody;
import org.diploma.app.controller.request.post.RequestModerationBody;
import org.diploma.app.controller.response.BadRequestBody;
import org.diploma.app.controller.response.DefaultBody;
import org.diploma.app.controller.response.ErrorBody;
import org.diploma.app.controller.response.InitBody;
import org.diploma.app.controller.response.ResponseStatisticBody;
import org.diploma.app.controller.response.ResponseTagBody;
import org.diploma.app.controller.response.dto.TagDto;
import org.diploma.app.model.db.entity.PostVotesStatistics;
import org.diploma.app.model.db.entity.PostsStatistics;
import org.diploma.app.model.db.entity.Tags;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.model.service.AuthService;
import org.diploma.app.model.service.CheckupService;
import org.diploma.app.model.service.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api")
class ApiGeneralController {

    @Autowired
    ApplicationContext context;

    @Autowired
    AuthService authService;

    @Autowired
    GeneralService generalService;

    //Метод смены флага модератора для удобства
    @GetMapping("/moderator")
    ResponseEntity<?> moderator(@RequestParam String email) {
        try {
            generalService.changeModeratorStatus(email);
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(400).body(new BadRequestBody("Пользователь " + email + " не найден"));
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/init")
    InitBody init() {
        return new InitBody("DevPub",
                "Рассказы разработчиков",
                "",
                "",
                "Рухлядко Сергей",
                "2020");
    }

    @GetMapping("/settings")
    ResponseEntity<?> settings(HttpSession session) {
        try {
            return ResponseEntity.ok(generalService.getAllSettings());
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/settings")
    ResponseEntity<?> settings(HttpSession session, @RequestBody HashMap<String, Boolean> settings) {
        try {
            generalService.changeSettings(session.getId(), settings);
        } catch(AccessDeniedException ad) {
            return ResponseEntity.status(400).body(new BadRequestBody("Пользователь не модератор"));
        } catch(EntityNotFoundException enf) {
            return ResponseEntity.status(400).body(new BadRequestBody(enf.getMessage()));
        } catch(AuthenticationCredentialsNotFoundException acnf) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/tag")
    ResponseEntity<?> tag() {
        List<TagDto> tagDtoList = new ArrayList<>();
        for(Tags tag : generalService.getAllTags())
            tagDtoList.add(new TagDto(tag.getName()));

        return ResponseEntity.ok(new ResponseTagBody(tagDtoList));
    }

    @PostMapping("/comment")
    ResponseEntity<?> comment(Principal principal, @RequestBody RequestCommentBody requestBody) {
        CheckupService checkupService = context.getBean("checkupService", CheckupService.class);
        checkupService.comment(requestBody.getText());
        Map<String, String> errors = checkupService.getErrors();

        if (!errors.isEmpty()) {
            return ResponseEntity.ok(new ErrorBody(errors));
        }

        try {
            int id = generalService.addComment(principal.getName(), requestBody.getParentId(), requestBody.getPostId(), requestBody.getText());
            Map<String, Integer> response = new HashMap<>();
            response.put("id", id);
            return ResponseEntity.ok(response);
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(400).body(new BadRequestBody("Соответствующие комментарий и/или пост не существуют"));
        }
    }

    @PostMapping("moderation")
    DefaultBody moderation(Principal principal, @RequestBody RequestModerationBody requestBody) {
        return new DefaultBody(generalService.changeModerationStatus(principal.getName(), requestBody.getPostId(), requestBody.getDecision()));
    }

    @GetMapping("/statistics/all")
    ResponseEntity<?> statisticsAll(HttpSession session) {
        if (!generalService.isEnabled(GlobalSetting.STATISTICS_IS_PUBLIC)) {
            Users user = authService.checkAuthentication(session.getId());
            if (!user.isModerator())
                return ResponseEntity.status(401).build();
        }

        PostsStatistics postsStatistics = generalService.getAllPostStatistics();
        PostVotesStatistics postVotesStatistics = generalService.getAllPostVoteStatistics();

        return ResponseEntity.ok(new ResponseStatisticBody(
            postsStatistics.getPostsCount(),
            postVotesStatistics.getLikesCount(),
            postVotesStatistics.getDislikesCount(),
            postsStatistics.getViewsCount(),
            postsStatistics.getFirstPublication()
        ));
    }

    @GetMapping("/statistics/my")
    ResponseStatisticBody statisticsMy(Principal principal) {
        PostsStatistics postsStatistics = generalService.getMyPostStatistics(principal.getName());
        PostVotesStatistics postVotesStatistics = generalService.getMyPostVotesStatistics(principal.getName());
        return new ResponseStatisticBody(
            postsStatistics.getPostsCount(),
            postVotesStatistics.getLikesCount(),
            postVotesStatistics.getDislikesCount(),
            postsStatistics.getViewsCount(),
            postsStatistics.getFirstPublication()
        );
    }
}