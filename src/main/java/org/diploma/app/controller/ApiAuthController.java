package org.diploma.app.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.request.RequestPasswordBody;
import org.diploma.app.controller.request.RequestRegisterBody;
import org.diploma.app.controller.request.RequestRestoreBody;
import org.diploma.app.controller.response.DefaultBody;
import org.diploma.app.controller.response.ResponseCaptchaBody;
import org.diploma.app.controller.response.ResponseDefaultBody;
import org.diploma.app.controller.response.ResponseErrorBody;
import org.diploma.app.controller.response.ResponseLoginCheckBody;
import org.diploma.app.controller.response.dto.FullUserDto;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.service.AuthService;
import org.diploma.app.model.service.CheckupService;
import org.diploma.app.model.service.PostService;
import org.diploma.app.model.service.RegistrationIsClosedException;
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

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;

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
    ResponseEntity<?> login(HttpSession session, @RequestBody HashMap<String ,String> loginBody) {
        Users user;
        try {
            user = authService.login(loginBody.get("e_mail"), loginBody.get("password"), session.getId());
        } catch(BadCredentialsException | EntityNotFoundException e) {
            return ResponseEntity.ok().body(new DefaultBody());
        }

        FullUserDto fullUserDto = new FullUserDto(
            user.getId(),
            user.getName(),
            user.getPhoto(),
            user.getEmail(),
            user.isModerator(),
            postService.moderationCount(user),
            user.isModerator());
        return ResponseEntity.ok(new ResponseLoginCheckBody(fullUserDto));
    }

    @GetMapping("/check")
    ResponseEntity<?> check(HttpSession session) {
        Users user;
        try {
            user = authService.checkAuthentication(session.getId());
        } catch(EntityNotFoundException | AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.ok().body(new DefaultBody());
        }

        FullUserDto fullUserDto = new FullUserDto(
            user.getId(),
            user.getName(),
            user.getPhoto(),
            user.getEmail(),
            user.isModerator(),
            postService.moderationCount(user),
            user.isModerator());
        return ResponseEntity.ok().body(new ResponseLoginCheckBody(fullUserDto));
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