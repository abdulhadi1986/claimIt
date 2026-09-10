package com.foundIt.claimIt.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityHeadersEnablerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
        response.addHeader("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains");
        response.addHeader("X-Content-Type-Options", "nosniff");
        response.addHeader("X-Frame-Options","DENY");
        response.addHeader("Content-Security-Policy","default-src 'self'; object-src 'none'; child-src 'self'; frame-ancestors 'none'; upgrade-insecure-requests; block-all-mixed-content");
        response.addHeader("Cache-Control", "no-store, max-age=0");
        response.addHeader("Pragma","no-cache");
        response.setHeader("server", "");
        filterChain.doFilter(request, response);
    }
}


