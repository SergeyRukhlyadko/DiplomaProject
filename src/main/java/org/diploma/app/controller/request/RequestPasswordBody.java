package org.diploma.app.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.service.CheckupService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class RequestPasswordBody {

    @NotBlank
    String code;

    @NotNull
    @Pattern(regexp = CheckupService.PASSWORD_REGEX, message = "Пароль может содержать только латинские символы и цифры")
    @Size(min = 6, max = 255, message = "Длинна пароля должна быть от {min} до {max} символов")
    String password;

    @NotNull
    @Size(min = 8, max = 10, message = "Длинна каптчи должна быть {min} символов")
    String captcha;

    @NotBlank
    @JsonProperty("captcha_secret")
    String captchaSecret;
}
