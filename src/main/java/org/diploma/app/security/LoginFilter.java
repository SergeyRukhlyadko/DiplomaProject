package org.diploma.app.security;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.diploma.app.controller.request.RequestLoginBody;
import org.diploma.app.validation.ValidationOrder;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class LoginFilter extends OncePerRequestFilter {

    private RequestMatcher requiresAuthenticationRequestMatcher = new AntPathRequestMatcher("/api/auth/login", "POST");
    private AuthenticationManager authenticationManager;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private Validator validator;
    private ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(
        AuthenticationManager authenticationManager,
        AuthenticationEntryPoint authenticationEntryPoint,
        Validator validator)
    {
        this.authenticationManager = authenticationManager;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.validator = validator;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws IOException, ServletException {
        if (requiresAuthentication(request)) {
            checkMediaTypeSupport(request.getHeader("Content-Type"));

            ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);
            RequestLoginBody body = readBody(inputMessage);
            validate(body);

            UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());

            Authentication authResult;
            try {
                authResult = authenticationManager.authenticate(authRequest);
            } catch (AuthenticationException e) {
                authenticationEntryPoint.commence(request, response, e);
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(authResult);
        }

        filterChain.doFilter(request, response);
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }

    private void checkMediaTypeSupport(String contentType) throws HttpMediaTypeNotSupportedException {
        List<MediaType> supportedMediaTypes = List.of();
        if (contentType == null) {
            throw new HttpMediaTypeNotSupportedException(null, supportedMediaTypes);
        }

        MediaType mediaType = MediaType.valueOf(contentType);
        boolean compatible = mediaType.isCompatibleWith(MediaType.APPLICATION_JSON);
        if (!compatible) {
            throw new HttpMediaTypeNotSupportedException(mediaType, supportedMediaTypes);
        }
    }

    private RequestLoginBody readBody(HttpInputMessage inputMessage) throws IOException {
        byte[] bytes = inputMessage.getBody().readAllBytes();
        if (bytes.length == 0) {
            throw new HttpMessageNotReadableException(
                "Required request body is missing: " + LoginFilter.class, inputMessage);
        }

        try {
            return objectMapper.readValue(bytes, RequestLoginBody.class);
        } catch (JsonParseException e) {
            throw new HttpMessageNotReadableException(e.getMessage(), e, inputMessage);
        }
    }

    private void validate(RequestLoginBody body) {
        Set<ConstraintViolation<RequestLoginBody>> errors = validator.validate(body, ValidationOrder.class);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }
    }
}
