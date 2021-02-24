package org.diploma.app.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.request.RequestPasswordBody;
import org.diploma.app.controller.request.RequestRegisterBody;
import org.diploma.app.controller.request.RequestRestoreBody;
import org.diploma.app.controller.request.ValidationOrder;
import org.diploma.app.controller.response.ResponseCaptchaBody;
import org.diploma.app.controller.response.ResponseDefaultBody;
import org.diploma.app.controller.response.ResponseErrorBody;
import org.diploma.app.controller.response.ResponseLoginCheckBody;
import org.diploma.app.model.auth.Captcha;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.service.AuthService;
import org.diploma.app.service.CheckupService;
import org.diploma.app.service.GeneralService;
import org.diploma.app.service.PostService;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/auth")
class ApiAuthController {

    ApplicationContext context;
    AuthService authService;
    PostService postService;
    GeneralService generalService;

    public ApiAuthController(ApplicationContext context, AuthService authService, PostService postService, GeneralService generalService) {
        this.context = context;
        this.authService = authService;
        this.postService = postService;
        this.generalService = generalService;
    }

    @PostMapping("/login")
    ResponseLoginCheckBody login(Principal principal) {
        Users user = generalService.findUser(principal.getName());
        boolean isModerator = user.isModerator();
        int moderationCount = isModerator ? postService.moderationCount() : 0;
        return new ResponseLoginCheckBody(
            user.getId(), user.getName(), user.getPhoto(), user.getEmail(), isModerator, moderationCount, isModerator
        );
    }

    @GetMapping("/check")
    ResponseLoginCheckBody check(Principal principal) {
        Users user = generalService.findUser(principal.getName());
        boolean isModerator = user.isModerator();
        int moderationCount = isModerator ? postService.moderationCount() : 0;
        return new ResponseLoginCheckBody(
            user.getId(), user.getName(), user.getPhoto(), user.getEmail(), isModerator, moderationCount, isModerator
        );
    }

    @PostMapping("/restore")
    ResponseDefaultBody restore(@Valid @RequestBody RequestRestoreBody requestBody) {
        return new ResponseDefaultBody(authService.restorePassword(requestBody.getEmail()));
    }

    @PostMapping("/password")
    ResponseEntity<?> password(@Valid @RequestBody RequestPasswordBody requestBody) {
        CheckupService checkupService = context.getBean("checkupService", CheckupService.class);
        checkupService.checkCaptcha(requestBody.getCaptcha(), requestBody.getCaptchaSecret());
        if (checkupService.containsErrors()) {
            return ResponseEntity.ok(new ResponseErrorBody(checkupService.getErrors()));
        }

        return ResponseEntity.ok(new ResponseDefaultBody(
            authService.changePassword(requestBody.getCode(), requestBody.getPassword()))
        );
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> register(@Validated(ValidationOrder.class) @RequestBody RequestRegisterBody body) {
        boolean isRegistered = authService.register(
            body.getName(), body.getEmail(), body.getPassword(), body.getCaptcha(), body.getCaptchaSecret()
        );

        if (isRegistered) {
            return ResponseEntity.ok(new ResponseDefaultBody(true));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/captcha")
    ResponseCaptchaBody captcha() {
        Captcha captcha = authService.getCaptcha();
        return new ResponseCaptchaBody(captcha.getSecret(), captcha.getImage());
    }
}