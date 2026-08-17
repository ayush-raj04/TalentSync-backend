package com.example.Talentsync.security;

import com.example.Talentsync.entity.User;
import com.example.Talentsync.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {


        // ==========================================
        // PUBLIC APIs
        // JWT ki zarurat nahi
        // ==========================================

        String path = request.getServletPath();

        if (
                path.equals("/api/login")
                        || path.equals("/api/signup")
                        || path.equals("/api/recruiter/signup")
                        || path.startsWith("/api/password/")
        ) {

            filterChain.doFilter(request, response);

            return;
        }


        // ==========================================
        // JWT Authentication
        // ==========================================

        String authHeader =
                request.getHeader("Authorization");


        if (
                authHeader != null
                        && authHeader.startsWith("Bearer ")
        ) {

            String token =
                    authHeader.substring(7);


            try {

                String email =
                        JwtUtil.extractEmail(token);


                User user =
                        userService.findByEmail(email);


                if (user != null) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    List.of(
                                            new SimpleGrantedAuthority(
                                                    user.getRole().name()
                                            )
                                    )
                            );


                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authentication
                            );
                }


            } catch (Exception e) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                response.getWriter().write(
                        "Invalid JWT Token"
                );

                return;
            }
        }


        filterChain.doFilter(
                request,
                response
        );
    }
}