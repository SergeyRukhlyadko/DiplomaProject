package org.diploma.app.controller.request.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class RequestPasswordBody {

    String code;

    String password;

    String captcha;

    @JsonProperty("captcha_secret")
    String captchaSecret;
}
