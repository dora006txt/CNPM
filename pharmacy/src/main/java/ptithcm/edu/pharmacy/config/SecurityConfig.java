package ptithcm.edu.pharmacy.config; // Adjust package name if needed

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // For CSRF disabling
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ptithcm.edu.pharmacy.security.JwtAuthenticationFilter;
import org.springframework.http.HttpMethod; // Import HttpMethod

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection - common for stateless REST APIs
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Allow public access to static resources (homepage, css, js)
                .requestMatchers("/", "/index.html", "/style.css", "/script.js").permitAll()
                // Allow public access to authentication endpoints
                .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/forgot-password").permitAll()
                // Allow public access to view categories and products (adjust as needed)
                .requestMatchers(HttpMethod.GET, "/api/categories/**", "/api/products/**").permitAll() // GET is public
                // Require ADMIN authority for managing categories/products (POST, PUT, DELETE)
                .requestMatchers(HttpMethod.POST, "/api/categories/**", "/api/products/**").hasAuthority("ADMIN") // Requires ADMIN
                .requestMatchers(HttpMethod.PUT, "/api/categories/**", "/api/products/**").hasAuthority("ADMIN") // Requires ADMIN
                .requestMatchers(HttpMethod.DELETE, "/api/categories/**", "/api/products/**").hasAuthority("ADMIN") // Requires ADMIN

                // Secure user profile and address endpoints
                .requestMatchers("/api/users/me").authenticated()
                .requestMatchers("/api/addresses/**").authenticated() // Secure all address endpoints

                // Configure other endpoint security (e.g., require authentication)
                // .requestMatchers("/api/orders/**").authenticated()
                // .requestMatchers("/api/admin/**").hasRole("ADMIN") // Example role-based access
                // Secure all other requests by default
                .anyRequest().authenticated()
            )
            // Configure session management to be stateless - common for APIs using tokens
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Add your JWT filter before the standard UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Add other configurations like exception handling, authentication provider, etc.

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use a strong password encoder
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}