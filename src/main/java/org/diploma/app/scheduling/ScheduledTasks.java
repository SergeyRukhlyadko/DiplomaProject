package org.diploma.app.scheduling;

import org.diploma.app.repository.CaptchaCodesRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class ScheduledTasks {

    private CaptchaCodesRepository captchaCodesRepository;

    public ScheduledTasks(CaptchaCodesRepository captchaCodesRepository) {
        this.captchaCodesRepository = captchaCodesRepository;
    }

    /*
        All captcha created over one hour are deleted
     */
    @Scheduled(cron = "@hourly")
    @Transactional
    public void clearCaptcha() {
        captchaCodesRepository.deleteByTimeLessThen(LocalDateTime.now().minusHours(1));
    }
}
