package org.diploma.app.service;

import org.diploma.app.model.auth.Captcha;
import org.diploma.app.model.db.entity.CaptchaCodes;
import org.diploma.app.repository.CaptchaCodesRepository;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CaptchaService {

    private CaptchaCodesRepository captchaCodesRepository;

    public CaptchaService(CaptchaCodesRepository captchaCodesRepository) {
        this.captchaCodesRepository = captchaCodesRepository;
    }

    @Lookup
    protected Captcha captcha() {
        return null;
    }

    /*
        A new captcha is created and stored in the database
     */
    @Transactional(rollbackFor = Exception.class)
    public Captcha create() {
        Captcha captcha = captcha();
        CaptchaCodes captchaCodes = new CaptchaCodes(LocalDateTime.now(), captcha.getCode(), captcha.getSecret());
        captchaCodesRepository.save(captchaCodes);
        return captcha;
    }

    /*
        @param captcha alphanumeric representation of captcha image
        @param secretCode unique captcha identifier
        @return false if arguments are null or blank, either if pair of captcha and secretCode not found, otherwise true
     */
    public boolean matches(String captcha, String secretCode) {
        if (captcha == null || secretCode == null) {
            return false;
        }

        if (captcha.isBlank() || secretCode.isBlank()) {
            return false;
        }

        return captchaCodesRepository.existsByCodeAndSecretCode(captcha, secretCode);
    }
}
