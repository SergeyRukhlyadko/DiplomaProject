package org.diploma.app.security;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private HandlerExceptionResolver resolver;

    public ExceptionHandlerFilter(HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws IOException, ServletException {
        try {
            filterChain.doFilter(request, response);
        } catch (HttpMediaTypeNotSupportedException | HttpMessageNotReadableException | ConstraintViolationException e) {
            resolver.resolveException(request, response, null, e);
        }
    }
}
