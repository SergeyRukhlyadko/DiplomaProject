package org.diploma.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.diploma.app.controller.request.RequestLoginBody;
import org.diploma.app.controller.request.ValidationOrder;
import org.diploma.app.controller.response.ResponseBadRequestBody;
import org.diploma.app.controller.response.ResponseErrorBody;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LoginFilter extends GenericFilterBean {

    private RequestMatcher requiresAuthenticationRequestMatcher = new AntPathRequestMatcher("/api/auth/login", "POST");
    private AuthenticationManager authenticationManager;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private Validator validator;
    private ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint, Validator validator) {
        this.authenticationManager = authenticationManager;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.validator = validator;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (requiresAuthentication(request)) {

            if (!isSupported(request.getHeader("Content-Type"))) {
                response.setStatus(400);
                response.getWriter().print(objectMapper.writeValueAsString(new ResponseBadRequestBody("Not supported content type")));
                return;
            }

            InputStream inputStream = request.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            if (bytes.length == 0) {
                response.setStatus(400);
                response.getWriter().print(objectMapper.writeValueAsString(new ResponseBadRequestBody("Invalid request message")));
                return;
            }

            RequestLoginBody body = objectMapper.readValue(bytes, RequestLoginBody.class);
            Set<ConstraintViolation<RequestLoginBody>> errors = validator.validate(body, ValidationOrder.class);
            if (!errors.isEmpty()) {
                Map<String, String> errorsMap = errors.stream().collect(Collectors.toMap(
                    cv -> cv.getPropertyPath().toString(), ConstraintViolation::getMessage));
                response.setStatus(200);
                response.getWriter().print(objectMapper.writeValueAsString(new ResponseErrorBody(errorsMap)));
                return;
            }

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

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }

    private boolean isSupported(String contentType) {
        if (contentType != null) {
            String[] contentTypeSplit = contentType.split(";");
            return MediaType.APPLICATION_JSON_VALUE.equals(contentTypeSplit[0]);
        }

        return false;
    }
}
