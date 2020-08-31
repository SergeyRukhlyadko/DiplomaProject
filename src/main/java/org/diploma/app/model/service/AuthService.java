package org.diploma.app.model.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.CaptchaCodes;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.model.db.repository.CaptchaCodesRepository;
import org.diploma.app.model.db.repository.UsersRepository;
import org.diploma.app.model.service.db.UsersDBService;
import org.diploma.app.model.util.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class AuthService {

    @Value("${spring.mail.username}")
    String from;

    @Value("${host}")
    String host;

    @Value("${captcha.outdated}")
    int captchaOutdated;

    @Autowired
    ApplicationContext context;

    @Autowired
    GeneralService generalService;

    @Autowired
    UsersDBService usersDBService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    CaptchaCodesRepository captchaCodesRepository;

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

    /*
        throws SQLQueryException
     */
    @Transactional
    public boolean restorePassword(String email) {
        if (usersRepository.existsUsersByEmail(email)) {
            String code = UUID.randomUUID().toString();
            if (usersRepository.updateCodeByEmail(code, email) != 1)
                throw new SQLQueryException("More than one row has been updated in the Users table with email: " + email);

            emailService.send(from, email, "Restore password", "http://" + host + "/login/change-password/" + code);
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

    /*
        throws RegistrationIsClosedException
     */
    public Users register(String name, String email, String password) throws RegistrationIsClosedException {
        if (generalService.isEnabled(GlobalSetting.MULTIUSER_MODE))
            return usersRepository.save(new Users(false, name, email, passwordEncoder.encode(password)));

        throw new RegistrationIsClosedException();
    }

    public Captcha createCaptcha() {
        Captcha captcha = context.getBean("captcha" , Captcha.class);

        LocalDateTime now = LocalDateTime.now();
        captchaCodesRepository.deleteByTimeLessThen(now.minusHours(1));

        CaptchaCodes captchaCodes = new CaptchaCodes(now, captcha.getCode(), captcha.getSecret());
        captchaCodesRepository.save(captchaCodes);

        return captcha;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    /*
        throws UserNotFoundException
     */
    public boolean isModerator(String email) {
        return usersRepository.isModeratorByEmail(email).orElseThrow(
            () -> new UserNotFoundException("User with email " + email + " not found")
        );
    }
}
