package org.diploma.app.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CheckAuthenticationFilter extends GenericFilterBean {

    private RequestMatcher requiresAuthenticationRequestMatcher = new AntPathRequestMatcher("/api/auth/check", "GET");
    private AuthenticationEntryPoint authenticationEntryPoint;

    public CheckAuthenticationFilter(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (requiresAuthenticationRequestMatcher.matches(request)) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.clearContext();
                authenticationEntryPoint.commence(request, response, new UserNotAuthenticatedException("User not authenticated"));
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
