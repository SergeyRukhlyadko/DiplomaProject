package org.diploma.app.scheduling.tasklet;

import org.diploma.app.repository.CaptchaCodesRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class CleaningCaptchaTasklet implements Tasklet {

    private long expires;
    private ChronoUnit expiresFormat;
    private CaptchaCodesRepository captchaCodesRepository;

    public CleaningCaptchaTasklet(
        @Value("${captcha.expires.format}") String expiresFormat,
        @Value("${captcha.expires}") long expires,
        CaptchaCodesRepository captchaCodesRepository
    ) {
        this.expiresFormat = ChronoUnit.valueOf(expiresFormat);
        this.expires = expires;
        this.captchaCodesRepository = captchaCodesRepository;
    }

    @Override
    @Nullable
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        captchaCodesRepository.deleteByTimeLessThen(LocalDateTime.now().minus(expires, expiresFormat));
        return RepeatStatus.FINISHED;
    }
}
