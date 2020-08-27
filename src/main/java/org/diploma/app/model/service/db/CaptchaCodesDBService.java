package org.diploma.app.model.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.CaptchaCodes;
import org.diploma.app.model.db.repository.CaptchaCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class CaptchaCodesDBService {

    @Autowired
    CaptchaCodesRepository captchaCodesRepository;

    public CaptchaCodes find(String secretCode) {
        return captchaCodesRepository.findBySecretCode(secretCode).orElseThrow(
            () -> new EntityNotFoundException("Captcha with secret code " + secretCode + " not found")
        );
    }
}
