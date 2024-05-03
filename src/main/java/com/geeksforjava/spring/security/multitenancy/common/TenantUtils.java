package com.geeksforjava.spring.security.multitenancy.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public interface TenantUtils {

    static String resolveTenantId(HttpServletRequest request) {
        List<String> segments
                = UriComponentsBuilder.fromPath(request.getRequestURI()).build().getPathSegments();

        if (segments.size() < 2) {
            throw new IllegalArgumentException();
        }

        return segments.get(0);
    }

}
