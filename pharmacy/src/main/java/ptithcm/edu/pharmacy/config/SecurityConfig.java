package ptithcm.edu.pharmacy.config; // Adjust package name if needed

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ptithcm.edu.pharmacy.security.JwtAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Permit public GET access to specific resources
                .requestMatchers(HttpMethod.GET,
                    "/", "/index.html", "/style.css", "/script.js", // Static content
                    // "/api/auth/login", // Moved below
                    "/api/auth/register", "/api/auth/forgot-password", // Auth endpoints (GET for potential pages)
                    "/api/categories/**", // View categories
                    "/api/products/**", // View products
                    "/api/v1/branches", "/api/v1/branches/**", // View branches
                    "/api/v1/inventory/branch/{branchId}/products/display", // View product list display per branch
                    "/api/v1/inventory/branch/{branchId}/product/{productId}/display" // View single product display
                ).permitAll()
                // Permit public POST access for authentication
                .requestMatchers(HttpMethod.POST,
                    "/api/auth/login", // <-- Allow POST for login
                    "/api/auth/register" // Allow POST for registration
                ).permitAll()

                // --- Branch Management (Admin) ---
                .requestMatchers(HttpMethod.POST, "/api/v1/branches").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/branches/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/branches/**").hasAuthority("ADMIN")

                // --- Inventory Management ---
                // Allow authenticated users to view specific raw inventory details
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/inventory", // Get all raw inventory  <-- This line
                    "/api/v1/inventory/{id}", // Get raw inventory by ID
                    "/api/v1/inventory/branch/{branchId}", // Get raw inventory by branch
                    "/api/v1/inventory/product/{productId}", // Get raw inventory by product
                    "/api/v1/inventory/branch/{branchId}/product/{productId}" // Get specific raw item
                 ).authenticated() // Requires authentication <-- This rule applies
                // Require ADMIN to modify inventory
                .requestMatchers(HttpMethod.POST, "/api/v1/inventory").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/inventory/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/inventory/**").hasAuthority("ADMIN")

                // --- Other Secured Endpoints ---
                .requestMatchers("/api/users/me").authenticated()
                .requestMatchers("/api/addresses/**").authenticated()

                // --- Cart Management ---
                .requestMatchers("/api/v1/cart/**").authenticated() // Secure all cart operations

                // Secure all other requests
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // --- THÊM CORS CONFIG ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // FE URL
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true); // Allow cookies, Authorization Header
    
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}