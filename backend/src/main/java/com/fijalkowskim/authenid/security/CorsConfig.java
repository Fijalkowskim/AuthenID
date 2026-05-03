package com.fijalkowskim.authenid.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS configuration allowing the React admin panel (http://localhost:3000) to call the backend.
 * <p>
 * Permits credentials so that the {@code Authorization} header is forwarded on cross-origin
 * requests from the frontend to the {@code /api/admin/**} resource server endpoints.
 * </p>
 */
@Configuration
public class CorsConfig {

    /**
     * Registers a {@link CorsConfigurationSource} bean used by Spring Security's
     * {@code .cors(Customizer.withDefaults())} in {@link SecurityConfig}.
     *
     * @return the configured CORS source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
