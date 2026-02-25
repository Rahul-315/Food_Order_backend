package com.online.ofos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth


                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/partner/register").permitAll()
                .requestMatchers("/api/restaurants", "/api/restaurants/**", "/api/menu").permitAll()
                .requestMatchers("/api/customer/profile").permitAll()
                .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                    ).permitAll()

                
            
                .requestMatchers(
                    "/api/admin/restaurants/**",
                    "/api/admin/menu/**",
                    "/api/admin/partner-restaurants/**"
                ).hasRole("ADMIN")

               
                .requestMatchers(
                    "/api/admin/users/**",
                    "/api/admin/orders/**"
                ).hasRole("ADMIN")

         
                .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                .requestMatchers(
                		"/api/staff/menu/**",
                		"/api/staff/orders/**"
                		).hasAnyRole("ADMIN","RESTAURANT_STAFF"
                				
                				)

         
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
