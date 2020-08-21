package org.diploma.app.model.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.GlobalSettings;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.model.service.db.CaptchaCodesDBService;
import org.diploma.app.model.service.db.GlobalSettingsDBService;
import org.diploma.app.model.service.db.UsersDBService;
import org.diploma.app.model.util.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class AuthService {

    @Autowired
    ApplicationContext context;

    @Value("${spring.mail.username}")
    String from;

    @Value("${host}")
    String host;

    @Autowired
    CaptchaCodesDBService captchaCodesDBService;

    @Autowired
    UsersDBService usersDBService;

    @Autowired
    GlobalSettingsDBService globalSettingsDBService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    public Captcha createCaptcha() {
        Captcha captcha = context.getBean("captcha" , Captcha.class);
        captchaCodesDBService.save(captcha.getSecret(), captcha.getCode());
        return captcha;
    }

    public Map<String, String> register(String name, String email, String password, String captcha, String captchaSecret) throws RegistrationIsClosedException {
        GlobalSettings globalSetting = globalSettingsDBService.find(GlobalSetting.MULTIUSER_MODE.toString());
        if (!globalSetting.isValue())
            throw new RegistrationIsClosedException();

        CheckupService checkupService = context.getBean("checkupService", CheckupService.class);
        checkupService.name(name);
        checkupService.email(email);
        checkupService.password(password);
        checkupService.captcha(captchaSecret, captcha);
        Map<String, String> errors = checkupService.getErrors();

        if (errors.isEmpty())
            usersDBService.save(false, name, email, passwordEncoder.encode(password));

        return errors;
    }

    public Users login(String email, String password, String sessionId) {
        Users user = usersDBService.find(email);

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new BadCredentialsException("Wrong password");

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            email,
            password,
            new ArrayList<>()
        );
        authentication.setDetails(sessionId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return user;
    }

    public Users checkAuthentication(String sessionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String details = String.valueOf(authentication.getDetails());

        if (!authentication.isAuthenticated() || !details.equals(sessionId))
            throw new AuthenticationCredentialsNotFoundException("User with session " + sessionId + " is not authorized");

        return usersDBService.find(String.valueOf(authentication.getPrincipal()));
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    public void relogin(String email, String sessionId) {
        logout();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            email,
            "",
            new ArrayList<>()
        );
        authentication.setDetails(sessionId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public boolean restorePassword(String email) {
        if (emailService.check(email) && usersDBService.exists(email)) {
            String code = UUID.randomUUID().toString();
            try {
                usersDBService.updateCodeByEmail(email, code);
                emailService.send(from, email, "Restore password", "http://" + host + "/login/change-password/" + code);
            } catch(EntityNotFoundException | MailException e) {
                return false;
            }

            return true;
        }

        return false;
    }

    public Map<String, String> changePassword(String code, String password, String captcha, String captchaSecret) {
        CheckupService checkupService = context.getBean("checkupService", CheckupService.class);
        checkupService.password(password);
        checkupService.captcha(captchaSecret, captcha);
        Map<String, String> errors = checkupService.getErrors();

        if (errors.isEmpty())
            usersDBService.updatePasswordByCode(code, passwordEncoder.encode(password));

        return errors;
    }
}
