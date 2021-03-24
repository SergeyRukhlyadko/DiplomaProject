package org.diploma.app.controller;

import org.diploma.app.controller.request.RequestPasswordBody;
import org.diploma.app.controller.request.RequestRegisterBody;
import org.diploma.app.controller.request.RequestRestoreBody;
import org.diploma.app.controller.response.ResponseCaptchaBody;
import org.diploma.app.controller.response.ResponseDefaultBody;
import org.diploma.app.controller.response.ResponseErrorBody;
import org.diploma.app.controller.response.ResponseLoginCheckBody;
import org.diploma.app.model.auth.Captcha;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.service.AuthService;
import org.diploma.app.service.CaptchaService;
import org.diploma.app.service.GeneralService;
import org.diploma.app.service.PostService;
import org.diploma.app.validation.ValidationOrder;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Locale;

@RestController
@RequestMapping("/api/auth")
class ApiAuthController {

    private AuthService authService;
    private PostService postService;
    private GeneralService generalService;
    private CaptchaService captchaService;
    private MessageSource messageSource;

    public ApiAuthController(
        AuthService authService,
        PostService postService,
        GeneralService generalService,
        CaptchaService captchaService,
        MessageSource messageSource
    ) {
        this.authService = authService;
        this.postService = postService;
        this.generalService = generalService;
        this.captchaService = captchaService;
        this.messageSource = messageSource;
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

    @PostMapping(value = "/restore", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseDefaultBody restorePassword(@Validated(ValidationOrder.class)  @RequestBody RequestRestoreBody body) {
        return new ResponseDefaultBody(authService.restorePassword(body.getEmail()));
    }

    @PostMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> changePassword(@Validated(ValidationOrder.class) @RequestBody RequestPasswordBody body) {
        return ResponseEntity.ok(new ResponseDefaultBody(
            authService.changePassword(body.getCode(), body.getPassword(), body.getCaptcha(), body.getCaptchaSecret()))
        );
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> register(@Validated(ValidationOrder.class) @RequestBody RequestRegisterBody body, Locale locale) {
        if (!captchaService.matches(body.getCaptcha(), body.getCaptchaSecret())) {
            return ResponseEntity.ok(
                new ResponseErrorBody("captcha", messageSource.getMessage("captcha.wrong.message", null, locale)));
        }

        authService.register(body.getName(), body.getEmail(), body.getPassword());
        return ResponseEntity.ok(new ResponseDefaultBody(true));
    }

    @GetMapping("/captcha")
    ResponseCaptchaBody captcha() {
        Captcha captcha = captchaService.create();
        return new ResponseCaptchaBody(captcha.getSecret(), captcha.getImage());
    }
}