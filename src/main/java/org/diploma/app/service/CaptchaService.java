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
}
