package org.diploma.app.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.request.RequestLoginBody;
import org.diploma.app.controller.request.RequestPasswordBody;
import org.diploma.app.controller.request.RequestRegisterBody;
import org.diploma.app.controller.request.RequestRestoreBody;
import org.diploma.app.controller.response.ResponseCaptchaBody;
import org.diploma.app.controller.response.ResponseDefaultBody;
import org.diploma.app.controller.response.ResponseErrorBody;
import org.diploma.app.controller.response.ResponseLoginCheckBody;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.service.AuthService;
import org.diploma.app.model.service.CheckupService;
import org.diploma.app.model.service.PostService;
import org.diploma.app.model.service.RegistrationIsClosedException;
import org.diploma.app.model.service.UserNotFoundException;
import org.diploma.app.model.util.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/auth")
class ApiAuthController {

    @Autowired
    ApplicationContext context;

    @Autowired
    AuthService authService;

    @Autowired
    PostService postService;

    @PostMapping("/login")
    ResponseEntity<?> login(HttpSession session, @Valid @RequestBody RequestLoginBody requestBody) {
        Users user;
        try {
            user = authService.login(requestBody.getEmail(), requestBody.getPassword(), session.getId());
        } catch(UserNotFoundException | BadCredentialsException e) {
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
        } catch(AuthenticationCredentialsNotFoundException e) {
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
        if (checkupService.containsErrors())
            return ResponseEntity.ok(new ResponseErrorBody(checkupService.getErrors()));

        return ResponseEntity.ok(new ResponseDefaultBody(
            authService.changePassword(requestBody.getCode(), requestBody.getPassword()))
        );
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@Valid @RequestBody RequestRegisterBody requestBody) {
        CheckupService checkupService = context.getBean("checkupService", CheckupService.class);
        checkupService
            .existsEmail(requestBody.getEmail())
            .checkCaptcha(requestBody.getCaptcha(), requestBody.getCaptchaSecret());

        if (checkupService.containsErrors())
            return ResponseEntity.ok(new ResponseErrorBody(checkupService.getErrors()));

        try {
            authService.register(requestBody.getName(), requestBody.getEmail(), requestBody.getPassword());
        } catch(RegistrationIsClosedException e) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(new ResponseDefaultBody(true));
    }

    @GetMapping("/captcha")
    ResponseCaptchaBody captcha() {
        Captcha captcha = authService.createCaptcha();
        return new ResponseCaptchaBody(captcha.getSecret(), captcha.getImage());
    }

    @GetMapping("/logout")
    ResponseDefaultBody logout() {
        authService.logout();
        return new ResponseDefaultBody(true);
    }
}