package com.geeksforjava.spring.security.multitenancy.config;

import com.geeksforjava.spring.security.multitenancy.core.security.TenantLoginUrlAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/*/login", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling((exceptions) -> exceptions.defaultAuthenticationEntryPointFor(
                        tenantLoginUrlAuthenticationEntryPoint(),
                        new AntPathRequestMatcher("/*/**")
                ));

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint tenantLoginUrlAuthenticationEntryPoint(){
        return new TenantLoginUrlAuthenticationEntryPoint("/login");
    }

}
