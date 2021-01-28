package org.diploma.app.model.auth;

import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.shredzone.commons.captcha.CaptchaGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Scope("prototype")
public class Captcha {

    final static int LEFT_BOUND = 48; // numeric '0'
    final static int RIGHT_BOUND = 122; //letter 'z'

    @Getter
    BufferedImage image;

    @Getter
    String code;

    @Getter
    String secret;

    public Captcha(CaptchaGenerator captchaGenerator, @Value("${captcha.length}") int length) {
        code = generateCode(length);
        image = captchaGenerator.createCaptcha(code.toCharArray());
        secret = UUID.randomUUID().toString();
    }

    private String generateCode(int length) {
        Random random = new Random();
        return random.ints(LEFT_BOUND, RIGHT_BOUND + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)) // 0-9, A-Z, a-z
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }
}
