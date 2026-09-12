package com.sehyeon.monfin.bank.config.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sehyeon.monfin.bank.security.BankAccountDetailsService;
import com.sehyeon.monfin.bank.security.JwtAuthFilter;

/** THIS CLASS HAS A LOT OF COMMENTS BECAUSE I AM LEARNING
 * This configuration runs right after frontend sends the backend a message
 * Typically before any authentication filters, which come before controllers
 * AKA first thing to ever run on the server when frontend sends a request
 * SpringSecurityConfig is used to control a custom behavior using @Bean
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final JwtAuthFilter jwtFilter;
    private final BankAccountDetailsService userDetailsService;

    /**
     * For normal users, logging into one or two accounts waiting 12 = 100 ms per request isn't long
     * For a hacker trying multiple passwords, it becomes an expensive call
     * The higher the factor, the longer the wait for the request to complete
     */
    private static final int BCRYPT_COST_FACTOR = 12;

    public SpringSecurityConfig(JwtAuthFilter jwtFilter, BankAccountDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Signup and login will not have jwt set in cookies
     * Therefore, allow any request from /signup and /login
     * @param http Created by Spring internally, used to define security rules (Creator)
     * @return SecurityFilterChain, which is the finished security object with the set rules (Enforcer)
     * @throws Exception Invalid Security Config | missing dependencies | invalid matcher config
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http
    ) throws Exception {
        return http
            .cors(Customizer.withDefaults())

            // CSRF = Cross-Site Request Forgery
            // Definition: web security vulnerability that tricks a user into performing unintended actions
            // on a web application where they are authenticated. This malicious website is opened while
            // the authenticated web app is also open in the same browser.
            // Line explanation: Create a token that is shared with the frontend
            // Whenever the frontend sends a request, they must add this csrf token
            // When the malicious site tries to send a fake request, they won't have this shared csrf token
            // Later, Spring Security will validate the csrf using the internal CsrfFilter
            //.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .csrf(csrf -> csrf.disable())

            // JWT doesn't exist during signup and login, so it should not go through JWT filtering
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/bank/auth/signup",
                    "/api/bank/auth/login",
                    // transaction requests involve payment processor requests
                    // and user may not be logged into their bank account
                    "/api/bank/transactions/authorize",
                    "/api/bank/transactions/verify-otp",
                    "/api/bank/payment/processor/credentials" // probably not the best way
                ).permitAll()
                .anyRequest().authenticated()
            )

            // Runs my jwt filter before Spring Security's built-in username/password authentication filter.
            .addFilterBefore(jwtFilter,
                    UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    // The type of password encryption used to encrypt passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        // generate random salt and combines with password for bcrypt algo
        return new BCryptPasswordEncoder(BCRYPT_COST_FACTOR);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) 
        throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Dao will call the userDetailService to get a bank account (by username)
        // and loads the BankAccountDetails (can't be entity cus it must implement UserDetails)
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider(userDetailsService);
        // Compare the saved, hashed password with the given, raw password using bcrypt encoder
        dao.setPasswordEncoder(passwordEncoder());
        return dao; // stands for data access object
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();

        // sites that are allowed to make requests to the server
        cors.setAllowedOrigins(List.of(
            "http://localhost:3001", // bank frontend (nextjs)
            "http://localhost:4200" // tsp form frontend (angular)
        ));

        // types of request methods allowed
        cors.setAllowedMethods(List.of(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "OPTIONS" // For preflights, like checking to see cors passes
        ));

        // cookies are allowed { credentials: "include" } (for jwt http only cookies)
        cors.setAllowCredentials(true);

        // csrf token cookies and content-type request headers are allowed
        // authorization (bearer) and any other custom headers are declined
        // cors.setAllowedHeaders(List.of(
        //     "Content-Type",
        //     "X-XSRF-TOKEN"
        // ));
        cors.setAllowedHeaders(List.of("*"));

        // Object that defines which url endpoints the cors rules apply to
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // apply rule to all endpoints
        source.registerCorsConfiguration("/**", cors);
        return source; // implements CorsConfigurationSource
    }
    
}
