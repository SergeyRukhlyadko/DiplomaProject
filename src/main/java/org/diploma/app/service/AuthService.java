package org.diploma.app.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.CaptchaCodes;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.repository.CaptchaCodesRepository;
import org.diploma.app.repository.UsersRepository;
import org.diploma.app.util.Captcha;
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
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    CaptchaCodesRepository captchaCodesRepository;

    /*
        throws UserNotFoundException, BadCredentialsException
     */
    public Users login(String email, String password, String sessionId) {
        Users user = usersRepository.findByEmail(email).orElseThrow(
            () -> new UserNotFoundException("User with email " + email + " not found")
        );

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new BadCredentialsException("Wrong password");

        authenticate(email, sessionId);
        return user;
    }

    /*
        throws AuthenticationCredentialsNotFoundException, UserNotFoundException
     */
    public Users checkAuthentication(String sessionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String details = String.valueOf(authentication.getDetails());

        if (!authentication.isAuthenticated() || !details.equals(sessionId))
            throw new AuthenticationCredentialsNotFoundException("User with session " + sessionId + " is not authorized");

        String email = String.valueOf(authentication.getPrincipal());
        return usersRepository.findByEmail(email).orElseThrow(
            () -> new UserNotFoundException("User with email " + email + " not found")
        );
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
    @Transactional(rollbackFor = Exception.class)
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

    /*
        throws SQLQueryException
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(String code, String password) {
        if (usersRepository.updatePasswordByCode(passwordEncoder.encode(password), code) != 1)
            throw new SQLQueryException("More than one row has been updated in the Users table with code: " + code);

        return true;
    }

    /*
        throws RegistrationIsClosedException
     */
    public Users register(String name, String email, String password) throws RegistrationIsClosedException {
        if (generalService.isEnabled(GlobalSetting.MULTIUSER_MODE))
            return usersRepository.save(new Users(false, name, email, passwordEncoder.encode(password)));

        throw new RegistrationIsClosedException();
    }

    @Transactional(rollbackFor = Exception.class)
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

    private void authenticate(String email, String sessionId) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            email,
            "",
            new ArrayList<>()
        );
        authentication.setDetails(sessionId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
