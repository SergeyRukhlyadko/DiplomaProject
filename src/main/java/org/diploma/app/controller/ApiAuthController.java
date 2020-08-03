package org.diploma.app.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.request.post.RequestRegisterBody;
import org.diploma.app.controller.request.post.RequestPasswordBody;
import org.diploma.app.controller.response.CaptchaBody;
import org.diploma.app.controller.response.DefaultBody;
import org.diploma.app.controller.response.ErrorBody;
import org.diploma.app.controller.response.ResponseLoginCheckBody;
import org.diploma.app.controller.response.dto.FullUserDto;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.service.AuthService;
import org.diploma.app.model.service.PostService;
import org.diploma.app.model.util.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/auth")
class ApiAuthController {

    @Autowired
    AuthService authService;

    @Autowired
    PostService postService;

    @GetMapping("/captcha")
    CaptchaBody captcha() {
        Captcha captcha = authService.createCaptcha();
        return new CaptchaBody(captcha.getSecret(), captcha.getImage());
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RequestRegisterBody requestBody) {
        Map<String, String> errors = authService.register(
            requestBody.getName(),
            requestBody.getEmail(),
            requestBody.getPassword(),
            requestBody.getCaptcha(),
            requestBody.getCaptchaSecret()
        );

        if (!errors.isEmpty())
            return ResponseEntity.ok(new ErrorBody(errors));

        return ResponseEntity.ok(new DefaultBody(true));
    }

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

    @GetMapping("/logout")
    DefaultBody logout() {
        authService.logout();
        return new DefaultBody(true);
    }

    @PostMapping("/restore")
    DefaultBody restore(@RequestBody HashMap<String, String> requestBody) {
        return new DefaultBody(authService.restorePassword(requestBody.get("email")));
    }

    @PostMapping("/password")
    ResponseEntity<?> password(@RequestBody RequestPasswordBody requestBody) {
        Map<String, String> errors = authService.changePassword(
            requestBody.getCode(),
            requestBody.getPassword(),
            requestBody.getCaptcha(),
            requestBody.getCaptchaSecret()
        );

        if (!errors.isEmpty())
            return ResponseEntity.ok(new ErrorBody(errors));

        return ResponseEntity.ok(new DefaultBody(true));
    }
}