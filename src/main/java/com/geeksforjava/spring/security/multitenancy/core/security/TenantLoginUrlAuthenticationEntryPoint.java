package com.geeksforjava.spring.security.multitenancy.core.security;

import com.geeksforjava.spring.security.multitenancy.common.TenantUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class TenantLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    public TenantLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response,
                                                     AuthenticationException exception) {
        log.debug("Determining multi-tenancy login url...");

        String tenantId = TenantUtils.resolveTenantId(request);
        log.trace("Resolved tenant identifier => {}", tenantId);

        return String.format("/%s/login", tenantId);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        super.commence(request, response, authException);
    }

}
