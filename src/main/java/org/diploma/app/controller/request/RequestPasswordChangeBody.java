package org.diploma.app.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.validation.FirstOrder;
import org.diploma.app.validation.SecondOrder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class RequestPasswordChangeBody {

    @NotBlank(groups = FirstOrder.class)
    String code;

    @NotBlank(groups = FirstOrder.class)
    @Size(message = "{password.length.message}", min = 6, groups = SecondOrder.class)
    String password;

    @NotBlank(groups = FirstOrder.class)
    String captcha;

    @NotBlank(groups = FirstOrder.class)
    @JsonProperty("captcha_secret")
    String captchaSecret;
}
