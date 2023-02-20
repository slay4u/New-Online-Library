package com.example.onlinelibrary.config;

import com.example.onlinelibrary.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    @Qualifier("delegatedAuthenticationEntryPoint")
    AuthenticationEntryPoint authEntryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Disable CSRF for now
        http.csrf().disable().authorizeRequests()
                .antMatchers("/online-library/v1/auth/**")
                .permitAll()
                .antMatchers("/online-library/v1/books/new/**").hasAuthority("ADMIN")
                .antMatchers("/online-library/v1/books/update/**").hasAuthority("ADMIN")
                .antMatchers("/online-library/v1/books/delete/**").hasAuthority("ADMIN")
                .antMatchers("/online-library/v1/books/photo/upload/**").hasAuthority("ADMIN")
                .antMatchers("/online-library/v1/authors/new/**").hasAuthority("ADMIN")
                .antMatchers("/online-library/v1/authors/update/**").hasAuthority("ADMIN")
                .antMatchers("/online-library/v1/authors/delete/**").hasAuthority("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authEntryPoint);

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
