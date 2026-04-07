package com.example.ShareMyRecipe.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.ShareMyRecipe.util.TokenUtil;


import java.io.IOException;
import java.util.List;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwtToken = request.getHeader("Authorization");
        String path = request.getRequestURI();

        if (path.contains("register") || path.contains("signin") || path.contains("verifyRegistrationToken")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing Authorization header");
            return;
        }

        // VALIDATE THE TOKEN
        Claims claims = TokenUtil.validateSignedToken(jwtToken);
        if (claims == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired JWT token");
            return;
        }

        String username = claims.getSubject();
        String role = claims.get("roles", String.class);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("register") || path.contains("signin") || path.contains("verifyRegistrationToken");
    }
}
