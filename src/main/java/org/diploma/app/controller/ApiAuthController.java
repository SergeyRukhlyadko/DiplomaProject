package org.diploma.app.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.request.RequestLoginBody;
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
import org.diploma.app.service.PostService;
import org.diploma.app.service.UserNotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/auth")
class ApiAuthController {

    ApplicationContext context;
    AuthService authService;
    PostService postService;

    public ApiAuthController(ApplicationContext context, AuthService authService, PostService postService) {
        this.context = context;
        this.authService = authService;
        this.postService = postService;
    }

    @PostMapping("/login")
    ResponseEntity<?> login(HttpSession session, @Valid @RequestBody RequestLoginBody requestBody) {
        Users user;
        try {
            user = authService.login(requestBody.getEmail(), requestBody.getPassword(), session.getId());
        } catch (UserNotFoundException | BadCredentialsException e) {
            //e.printStackTrace();
            return ResponseEntity.ok().body(new ResponseDefaultBody());
        }

        return ResponseEntity.ok(new ResponseLoginCheckBody(
            user.getId(),
            user.getName(),
            user.getPhoto(),
            user.getEmail(),
            user.isModerator(),
            user.isModerator() ? postService.moderationCount() : 0,
            user.isModerator()
        ));
    }

    @GetMapping("/check")
    ResponseEntity<?> check(HttpSession session) {
        Users user;
        try {
            user = authService.checkAuthentication(session.getId());
        } catch (AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.ok().body(new ResponseDefaultBody());
        }

        return ResponseEntity.ok(new ResponseLoginCheckBody(
            user.getId(),
            user.getName(),
            user.getPhoto(),
            user.getEmail(),
            user.isModerator(),
            user.isModerator() ? postService.moderationCount() : 0,
            user.isModerator()
        ));
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

    @GetMapping("/logout")
    ResponseDefaultBody logout() {
        authService.logout();
        return new ResponseDefaultBody(true);
    }
}