package org.diploma.app.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.repository.CaptchaCodesRepository;
import org.diploma.app.repository.UsersRepository;
import org.diploma.app.service.db.UsersDBService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Scope("prototype")
public class CheckupService {

    public static final String NAME_REGEX = "([A-Za-z- ]+)|([А-Яа-я- ]+)";
    public static final String PASSWORD_REGEX = "[A-Za-z0-9]+";

    @Getter
    Map<String, String> errors = new HashMap<>();

    UsersDBService usersDBService;
    EmailService emailService;
    UsersRepository usersRepository;
    CaptchaCodesRepository captchaCodesRepository;

    public CheckupService(
        UsersDBService usersDBService,
        EmailService emailService,
        UsersRepository usersRepository,
        CaptchaCodesRepository captchaCodesRepository
    ) {
        this.usersDBService = usersDBService;
        this.emailService = emailService;
        this.usersRepository = usersRepository;
        this.captchaCodesRepository = captchaCodesRepository;
    }

    @Deprecated
    public boolean containsErrors() {
        return !errors.isEmpty();
    }

    public CheckupService name(String name) {
        Optional.ofNullable(name).ifPresent((n) -> {
            if (!n.matches(NAME_REGEX)) {
                errors.put("name", "Имя указано неверно");
            }
        });

        return this;
    }

    public CheckupService email(String email) {
        Optional.ofNullable(email).ifPresent((e) -> {
            if (emailService.check(e)) {
                if (usersDBService.exists(e)) {
                    errors.put("email", "Этот e-mail уже зарегистрирован");
                }
            } else {
                errors.put("email", "Неверный формат e-mail");
            }
        });

        return this;
    }

    public CheckupService password(String password) {
        Optional.ofNullable(password).ifPresent((p) -> {
            if (p.length() > 5) {
                if (!p.matches(PASSWORD_REGEX)) {
                    errors
                        .put("password", "Пароль может содержать только латинские символы и цифры");
                }
            } else {
                errors.put("password", "Пароль короче 6-ти символов");
            }
        });

        return this;
    }

    @Deprecated
    public void comment(String text) {
        if (text.length() < 2) {
            errors.put("text", "Текст комментария не задан или слишком короткий");
        }
    }

    public CheckupService imageSize(long size) {
        if (size == 0) {
            errors.put("image", "Пустой файл");
        } else if ((size / 1024) > 5120 /*kilobytes*/) {
            errors.put("image", "Размер файла превышает допустимый размер");
        }

        return this;
    }

    public CheckupService imageFormat(String format) {
        Optional.ofNullable(format).ifPresent((f) -> {
            if (!f.equals("JPEG") && !f.equals("PNG")) {
                errors.put("image", "Неверный формат файла");
            }
        });

        return this;
    }

    @Deprecated
    public CheckupService removePhoto(String photo, int removePhoto) {
        Optional.ofNullable(photo).ifPresent((p) -> {
            if (!p.isEmpty() && removePhoto != 1) {
                errors.put("photo", "Параметры на удалние фотографии не верны");
            }
        });

        return this;
    }

    public CheckupService changePhoto(int removePhoto) {
        if (removePhoto != 0) {
            errors.put("photo", "Параметры на изменение фотографии не верны");
        }

        return this;
    }

    @Deprecated
    public CheckupService existsEmail(String email) {
        if (usersRepository.existsUsersByEmail(email)) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }

        return this;
    }

    @Deprecated
    public CheckupService checkCaptcha(String code, String secretCode) {
        if (!captchaCodesRepository.existsByCodeAndSecretCode(code, secretCode)) {
            errors.put("captcha", "Код с картинки введён неверно");
        }

        return this;
    }
}
