package org.diploma.app.model.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.service.db.CaptchaCodesDBService;
import org.diploma.app.model.service.db.UsersDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    public void comment(String text) {
        if (text.length() < 2)
            errors.put("text", "Текст комментария не задан или слишком короткий");
    }

    public void title(String title) {
        if (title.isEmpty()) {
            errors.put("title", "Заголовок не установлен");
        } else if (title.length() < 3) {
            errors.put("title", "Заголовок слишком короткий");
        }
    }

    public void text(String text) {
        if (text.isEmpty()) {
            errors.put("text", "Текст публикации не установлен");
        } else if (text.length() < 50) {
            errors.put("text", "Текст публикации слишком короткий");
        }
    }

    public void imageSize(long size) {
        if (size == 0) {
            errors.put("image", "Пустой файл");
        } else if ((size / 1024) > 5120 /*kilobytes*/) {
            errors.put("image", "Размер файла превышает допустимый размер");
        }
    }

    public void imageFormat(String format) {
        if (!format.equals("JPEG") && !format.equals("PNG"))
            errors.put("image", "Неверный формат файла");
    }
}
