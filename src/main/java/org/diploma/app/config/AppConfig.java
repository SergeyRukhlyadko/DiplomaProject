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

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    public CaptchaGenerator captchaGenerator() {
        return new DefaultCaptchaGenerator();
    }

    @Bean
    public Validator validator() {
        final ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
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
