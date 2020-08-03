package org.diploma.app.controller.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class CaptchaBody {

    String secret;
    String image = "data:image/jpeg;base64, ";

    @SneakyThrows(IOException.class)
    public CaptchaBody(String secret, BufferedImage captcha) {
        this.secret = secret;

        try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(captcha, "jpeg", os);
            os.flush();
            image += Base64.getEncoder().encodeToString(os.toByteArray());
        }
    }
}
