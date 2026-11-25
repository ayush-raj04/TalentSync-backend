
package com.example.Talentsync.config;

import com.example.Talentsync.security.JwtFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    // Password Encoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    // CORS Configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.asList(
                        "https://talentsync-ch00.onrender.com",
                        "http://localhost:5173"
                )
        );

        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                Arrays.asList("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // Security Configuration
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Public APIs
                        .requestMatchers(
                                "/",
                                "/api/signup",
                                "/api/login",
                                "/api/recruiter/signup",
                                "/api/password/forgot",
                                "/api/password/reset"
                        ).permitAll()

                        // Recruiter
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/jobs"
                        ).hasAuthority("RECRUITER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/jobs/my"
                        ).hasAuthority("RECRUITER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/job/**"
                        ).hasAuthority("RECRUITER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/applications/*/status"
                        ).hasAuthority("RECRUITER")

                        // Interview - Recruiter
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/interviews/schedule/**"
                        ).hasAuthority("RECRUITER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/interviews/recruiter"
                        ).hasAuthority("RECRUITER")

                        // Recruiter - Resume
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/*/resume"
                        ).hasAuthority("RECRUITER")

                        // Candidate
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/applications/apply"
                        ).hasAuthority("CANDIDATE")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/my"
                        ).hasAuthority("CANDIDATE")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/dashboard/candidate"
                        ).hasAuthority("CANDIDATE")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/applications/*/uploadResume"
                        ).hasAuthority("CANDIDATE")

                        // Interview - Candidate
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/interviews/candidate"
                        ).hasAuthority("CANDIDATE")

                        // Admin - Job Management
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/jobs/admin/companies"
                        ).hasAuthority("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/jobs/admin/company"
                        ).hasAuthority("ADMIN")

                        // Common Jobs
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/jobs"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/jobs/{id}"
                        ).authenticated()

                        // Admin
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasAuthority("ADMIN")

                        // User Profile
                        .requestMatchers(
                                "/api/user/profile"
                        ).authenticated()

                        // Everything Else
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
