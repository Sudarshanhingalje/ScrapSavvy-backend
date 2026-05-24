package com.scrap.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class JwtResponseFilter implements Filter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // optional
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String header = httpRequest.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                if (jwtTokenUtil.validateToken(token)) {

                    // Optional: extract data if needed
                    String username = jwtTokenUtil.getUsernameFromToken(token);
                    Long userId = jwtTokenUtil.extractUserId(token);

                    // You can log or debug
                    // System.out.println("User: " + username + " ID: " + userId);

                }
            } catch (Exception e) {
                // Invalid token → ignore or log
                System.out.println("Invalid JWT: " + e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // optional
    }
}