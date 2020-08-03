package org.diploma.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"org.shredzone.commons.captcha.impl", "org.diploma.app"})
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}