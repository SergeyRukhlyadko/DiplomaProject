package org.diploma.app.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.validation.Validator;

@EnableWebSecurity//(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsByEmailService userDetailsService;
    private Validator validator;
    private HandlerExceptionResolver resolver;

    public SecurityConfig(
        UserDetailsByEmailService userDetailsService,
        Validator validator,
        @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
    ) {
        this.userDetailsService = userDetailsService;
        this.validator = validator;
        this.resolver = resolver;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationExceptionEntryPoint();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers(HttpMethod.PUT, "/api/post", "/api/settings").authenticated()
                .antMatchers(HttpMethod.POST, "/api/post", "/api/post/like", "/api/post/dislike").authenticated()
                .antMatchers("/api/post/my", "/api/post/moderation", "/api/moderation",
                    "/api/comment", "/api/statistics/my", "/api/profile/my", "/api/image").authenticated()
                .anyRequest().permitAll()
            .and()
            .addFilterBefore(new ExceptionHandlerFilter(resolver), LogoutFilter.class)
            .addFilterAfter(new CheckAuthenticationFilter(authenticationEntryPoint()), LogoutFilter.class)
            .addFilterAfter(
                new LoginFilter(authenticationManagerBean(), authenticationEntryPoint(), validator), LogoutFilter.class)
            .logout()
                .logoutUrl("/api/auth/logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(new JsonReturningLogoutSuccessHandler())
            .and()
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}
