package org.diploma.app.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class AuthService {

    private AuthenticationTrustResolver authenticationTrustResolver;
    String from;
    String host;
    EmailService emailService;
    PasswordEncoder passwordEncoder;
    UsersRepository usersRepository;

    public AuthService(
        @Value("${spring.mail.username}") String from,
        @Value("${host}") String host,
        EmailService emailService,
        PasswordEncoder passwordEncoder,
        UsersRepository usersRepository
    ) {
        this.authenticationTrustResolver = new AuthenticationTrustResolverImpl();
        this.from = from;
        this.host = host;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
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

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.isAuthenticated() && !authenticationTrustResolver.isAnonymous(authentication);
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
        Update password by recovery code
        @throws SQLQueryException if updated more than one row
     */
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String code, String password) {
        int updatedRows = usersRepository.updatePasswordByCode(passwordEncoder.encode(password), code);
        if (updatedRows > 1) {
            throw new SQLQueryException(
                updatedRows + " rows have been updated in the Users table with code: " + code);
        }
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
