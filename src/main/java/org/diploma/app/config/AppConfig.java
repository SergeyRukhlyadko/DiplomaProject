package org.diploma.app.config;

import org.hibernate.validator.HibernateValidator;
import org.shredzone.commons.captcha.CaptchaGenerator;
import org.shredzone.commons.captcha.impl.DefaultCaptchaGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Locale;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    public CaptchaGenerator captchaGenerator() {
        return new DefaultCaptchaGenerator();
    }

    @Bean
    public HibernateLocalResolver hibernateLocalResolver() {
        return new HibernateLocalResolver();
    }

    @Bean
    public Validator validator() {
        final ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
            .locales(Locale.ENGLISH, Locale.forLanguageTag("ru"))
            .localeResolver(hibernateLocalResolver())
            .propertyNodeNameProvider(new JacksonPropertyNodeNameProvider())
            .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(validator());
        return methodValidationPostProcessor;
    }
}
