package com.elec_business.user.jwt;

import com.elec_business.exception.EmailNotVerifiedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Throwable cause = authException.getCause();

        if (cause instanceof EmailNotVerifiedException) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Email not verified.");
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password.");
        }
    }
}
