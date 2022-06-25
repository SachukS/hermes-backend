package com.hysens.hermes.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .antMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**")
                            .permitAll()
                        .antMatchers("/swagger-ui")
                            .permitAll()
                        .antMatchers("/api/v1/user/login", "/api/v1/user/register")
                            .permitAll()
                        .antMatchers("/**")
                            .authenticated()
                )
                .csrf().disable()
                .cors().and()
                .httpBasic(withDefaults());
        return http.build();
    }

}