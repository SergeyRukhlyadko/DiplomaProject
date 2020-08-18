package org.diploma.app.model.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers(HttpMethod.PUT, "/api/post", "/api/settings").authenticated()
                .antMatchers(HttpMethod.POST, "/api/post", "/api/post/like", "/api/post/dislike").authenticated()
                .antMatchers("/api/auth/logout", "/api/post/my", "/api/post/moderation",
                    "/api/moderation", "/api/comment", "/api/statistics/my", "/api/profile/my", "/api/image").authenticated()
                .anyRequest().permitAll();
    }
}
