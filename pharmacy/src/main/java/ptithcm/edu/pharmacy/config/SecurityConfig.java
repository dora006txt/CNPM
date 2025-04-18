package ptithcm.edu.pharmacy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Import BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Define the SecurityFilterChain bean (as before)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Authorize HTTP requests
            .authorizeHttpRequests(authz -> authz
                // Permit all requests starting with /api/
                .requestMatchers("/api/**").permitAll()
                // Any other request needs to be authenticated (optional, good practice)
                .anyRequest().authenticated()
            )
            // Disable CSRF protection - Common for stateless APIs (like REST APIs often are)
            // Re-evaluate if your API uses sessions or requires CSRF protection
            .csrf(AbstractHttpConfigurer::disable);
            // You might add other configurations like formLogin, httpBasic, etc. if needed later

        return http.build();
    }

    // Expose AuthenticationManager as a Bean (as before)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // --- Define a PasswordEncoder Bean ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCrypt for strong, salted password hashing
        return new BCryptPasswordEncoder();
    }
}