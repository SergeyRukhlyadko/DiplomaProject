package org.diploma.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@EnableWebSecurity//(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /*@Autowired
    private UserDetailsByEmailService userDetailsByEmailService;*/

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
                .antMatchers("/api/auth/logout", "/api/post/my", "/api/post/moderation", "/api/moderation",
                    "/api/comment", "/api/statistics/my", "/api/profile/my", "/api/image").authenticated()
                .anyRequest().permitAll()
            .and()
            .addFilterAfter(new CheckAuthenticationFilter(authenticationEntryPoint()), LogoutFilter.class)
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());
    }

    /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsByEmailService).passwordEncoder(passwordEncoder());
    }*/
}
