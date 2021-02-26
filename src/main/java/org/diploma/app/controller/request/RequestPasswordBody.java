package org.diploma.app.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class RequestPasswordBody {

    @NotBlank(groups = FirstOrder.class)
    String code;

    @NotBlank(groups = FirstOrder.class)
    @Size(min = 6, message = "длинна пароля должна быть не менее {min} символов", groups = SecondOrder.class)
    String password;

    @NotBlank(groups = FirstOrder.class)
    String captcha;

    @NotBlank(groups = FirstOrder.class)
    @JsonProperty("captcha_secret")
    String captchaSecret;
}
