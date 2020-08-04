package org.diploma.app.model.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.service.db.CaptchaCodesDBService;
import org.diploma.app.model.service.db.UsersDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Service
@Scope("prototype")
public class CheckupService {

    @Autowired
    UsersDBService usersDBService;

    @Autowired
    CaptchaCodesDBService captchaCodesDBService;

    @Autowired
    EmailService emailService;

    static final String NAME_REGEX = "([A-Z]{1}[a-z]+)|([А-я]{1}[а-я]+)";
    static final String PASSWORD_REGEX = "[A-Za-z0-9]+";

    Map<String, String> errors = new HashMap<>();

    public void name(String name) {
        if (!name.matches(NAME_REGEX))
            errors.put("name", "Имя указано неверно");
    }

    public void email(String email) {
        if (emailService.check(email)) {
            if (usersDBService.exists(email))
                errors.put("email", "Этот e-mail уже зарегистрирован");
        } else  {
            errors.put("email", "Неверный формат e-mail");
        }
    }

    public void password(String password) {
        if (password.length() > 5) {
            if (!password.matches(PASSWORD_REGEX))
                errors.put("password", "Пароль может содержать только латинские символы и цифры");
        } else {
            errors.put("password", "Пароль короче 6-ти символов");
        }
    }

    public void captcha(String captchaSecret, String captcha) {
        if (!captchaCodesDBService.find(captchaSecret).getCode().equals(captcha))
            errors.put("captcha", "Код с картинки введён неверно");
    }
}
