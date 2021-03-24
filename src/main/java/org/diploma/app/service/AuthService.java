package org.diploma.app.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.auth.EmailAlreadyExistsException;
import org.diploma.app.model.auth.InvalidCaptchaException;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.repository.CaptchaCodesRepository;
import org.diploma.app.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class AuthService {

    String from;
    String host;
    EmailService emailService;
    PasswordEncoder passwordEncoder;
    UsersRepository usersRepository;
    CaptchaCodesRepository captchaCodesRepository;

    public AuthService(
        @Value("${spring.mail.username}") String from,
        @Value("${host}") String host,
        EmailService emailService,
        PasswordEncoder passwordEncoder,
        UsersRepository usersRepository,
        CaptchaCodesRepository captchaCodesRepository
    ) {
        this.from = from;
        this.host = host;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
        this.captchaCodesRepository = captchaCodesRepository;
    }

    //temporary implementation for ApiGeneralController.statisticsAll and ApiPostController.postId
    public Users checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            String email = String.valueOf(authentication.getName());
            return usersRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User with email " + email + " not found")
            );
        } else {
            throw new AuthenticationCredentialsNotFoundException("User is not authorized");
        }
    }

    /*
        throws SQLQueryException
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean restorePassword(String email) {
        if (usersRepository.existsUsersByEmail(email)) {
            String code = UUID.randomUUID().toString();
            if (usersRepository.updateCodeByEmail(code, email) != 1) {
                throw new SQLQueryException(
                    "More than one row has been updated in the Users table with email: " + email);
            }

            emailService.send(from, email, "Restore password",
                "http://" + host + "/login/change-password/" + code);
            return true;
        }

        return false;
    }

    /*
        @return true if password changed
        @throws SQLQueryException if updated more then one row
        @throws InvalidCaptchaException if captcha is not valid
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(String code, String password, String captcha, String secretCode) {
        boolean isCaptchaValid = verifyCaptcha(captcha, secretCode);
        if (isCaptchaValid) {
            int numberOftUpdatedRows = usersRepository.updatePasswordByCode(passwordEncoder.encode(password), code);
            if (numberOftUpdatedRows != 1) {
                throw new SQLQueryException(
                    "More than one row has been updated in the Users table with code: " + code);
            }
        } else {
            throw new InvalidCaptchaException("Invalid " + captcha + " captcha");
        }

        return true;
    }

    /*
        @throws EmailAlreadyExistsException if user with given email already exists
     */
    public void register(String name, String email, String password) {
        if (usersRepository.existsUsersByEmail(email)) {
            throw new EmailAlreadyExistsException("Email " + email + " already exists");
        }

        usersRepository.save(new Users(false, name, email, passwordEncoder.encode(password)));
    }

    /*
        @param captcha alphanumeric representation of captcha image
        @param secretCode unique captcha identifier
        @return false if arguments is null or blank, either if pair of captcha and secretCode not found, otherwise true
     */
    public boolean verifyCaptcha(String captcha, String secretCode) {
        if (captcha == null || secretCode == null) {
            return false;
        }

        if (captcha.isBlank() || secretCode.isBlank()) {
            return false;
        }

        return captchaCodesRepository.existsByCodeAndSecretCode(captcha, secretCode);
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
