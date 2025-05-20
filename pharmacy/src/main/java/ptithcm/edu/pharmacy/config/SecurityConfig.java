package ptithcm.edu.pharmacy.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import lombok.RequiredArgsConstructor;
import ptithcm.edu.pharmacy.security.JwtAuthenticationFilter;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Add if needed for AuthenticationProvider bean
import org.springframework.security.core.userdetails.UserDetailsService; // Add if needed for AuthenticationProvider bean

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Đảm bảo đã có
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    // --- Add UserDetailsService injection if needed for AuthenticationProvider ---
    @Autowired
    private UserDetailsService userDetailsService;
    // --- End injection ---

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Permit public GET access to specific resources
                        .requestMatchers(HttpMethod.GET,
                                "/", "/index.html", "/style.css", "/script.js", // Static content
                                "/api/auth/register", "/api/auth/forgot-password", // Auth endpoints
                                "/api/categories/**", // View categories
                                "/api/products/**", // View products
                                "/api/v1/branches", "/api/v1/branches/**", // View branches
                                "/api/v1/inventory/branch/{branchId}/products/display",
                                "/api/v1/inventory/branch/{branchId}/product/{productId}/display",
                                "/api/public/doctors", "/api/public/doctors/**",
                                "/api/banners/active",
                                "/api/manufacturers", "/api/manufacturers/**",
                                "/api/brands", "/api/brands/**"
                                // Remove "/ws/**" from here
                        ).permitAll()
                        // Permit public POST access for authentication AND forgot password
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/forgot-password"
                        ).permitAll()
                        .requestMatchers("/ws/**").permitAll()

                        // --- Branch Management (Admin) ---
                        .requestMatchers(HttpMethod.POST, "/api/v1/branches").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/branches/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/branches/**").hasAuthority("ADMIN")

                        // --- Inventory Management ---
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/inventory",
                                "/api/v1/inventory/{id}",
                                "/api/v1/inventory/branch/{branchId}",
                                "/api/v1/inventory/product/{productId}",
                                "/api/v1/inventory/branch/{branchId}/product/{productId}")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/inventory").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/inventory/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/inventory/**").hasAuthority("ADMIN")

                        // --- Payment Type Management ---
                        .requestMatchers(HttpMethod.GET, "/api/payment-types").authenticated()
                        .requestMatchers("/api/admin/payment-types/**").hasAuthority("ADMIN")

                        // --- Shipping Method Management (Baseline) ---
                        .requestMatchers("/api/shipping-methods/**").authenticated()

                        // --- Order Management (Authenticated Users) ---
                        .requestMatchers("/api/v1/orders", "/api/v1/orders/**").authenticated() // User's own orders
                        
                        // --- NEW: Order Management (Admin) ---
                        .requestMatchers("/api/v1/orders/admin/**").hasAuthority("ADMIN") // Secure admin order endpoints

                        // --- Other Secured Endpoints ---
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/addresses/**").authenticated()
                        // --- Cart Management (Authenticated Users) ---
                        // Thay thế dòng .requestMatchers("/api/v1/cart/**").authenticated() bằng các dòng sau:
                        .requestMatchers(HttpMethod.POST, "/api/v1/cart/items").authenticated() // Thêm sản phẩm vào giỏ hàng
                        .requestMatchers(HttpMethod.GET, "/api/v1/cart").authenticated()       // Xem giỏ hàng
                        .requestMatchers(HttpMethod.PUT, "/api/v1/cart/items/{cartItemId}").authenticated() // Cập nhật số lượng sản phẩm
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cart/items/{cartItemId}").authenticated() // Xóa sản phẩm khỏi giỏ hàng

                        // --- Staff Management (Admin) ---
                        .requestMatchers("/api/admin/staff/**").hasAuthority("ADMIN")

                        // --- Promotion Management (Admin) ---
                        .requestMatchers("/api/admin/promotions/**").hasAuthority("ADMIN")

                        // --- Banner Management (Admin) ---
                        .requestMatchers("/api/admin/banners/**").hasAuthority("ADMIN")

                        // --- Manufacturer Management (Admin for CUD, public for R) ---
                        .requestMatchers(HttpMethod.POST, "/api/manufacturers").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/manufacturers/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/manufacturers/**").hasAuthority("ADMIN")

                        // --- Brand Management (Admin for CUD, public for R already handled above) ---
                        .requestMatchers(HttpMethod.POST, "/api/brands").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/brands/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/brands/**").hasAuthority("ADMIN")

                        // --- User Management (Admin) ---
                        .requestMatchers("/api/admin/users/**").hasAuthority("ADMIN")

                        // Secure all other requests
                        .requestMatchers(HttpMethod.GET, "/api/chat/history/**").authenticated() // Thêm dòng này
                        .anyRequest().authenticated()
                ) // Closes authorizeHttpRequests lambda
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- Add this Bean definition ---
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    // --- End of Bean definition ---

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- Define the corsConfigurationSource Bean ---
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Configure allowed origins (e.g., your frontend URL)
        // Use "*" for development/testing, but be more specific in production
        configuration
                .setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8081", "http://localhost:5173")); // Example
                                                                                                                        // origins
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true); // Allow credentials

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this configuration to all paths
        return source;
    }
    // --- End corsConfigurationSource Bean ---

    // --- Define the AuthenticationProvider Bean ---
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Set the UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder()); // Set the PasswordEncoder
        return authProvider;
    }
    // --- End AuthenticationProvider Bean ---
}