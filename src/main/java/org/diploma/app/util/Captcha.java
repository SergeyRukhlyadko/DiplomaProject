package org.diploma.app.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.shredzone.commons.captcha.CaptchaGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Component
@Scope("prototype")
public class Captcha {

    BufferedImage image;

    String code;

    String secret;

    public Captcha(CaptchaGenerator captchaGenerator, @Value("${captcha.length}") int length) {
        char[] codeSequence = new CodeGenerator().generate(length);
        image = captchaGenerator.createCaptcha(codeSequence);
        code = String.valueOf(codeSequence);
        secret = UUID.randomUUID().toString();
    }
}
