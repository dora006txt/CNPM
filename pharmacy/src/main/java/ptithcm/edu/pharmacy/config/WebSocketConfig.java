package ptithcm.edu.pharmacy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // Thêm import này
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import ptithcm.edu.pharmacy.security.JwtUtils; // Thay đổi import này

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // Đảm bảo interceptor này chạy sau các interceptor bảo mật mặc định
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private JwtUtils jwtUtils; // Thay đổi từ JwtTokenProvider sang JwtUtils

    @Autowired
    private UserDetailsService userDetailsService; // Inject UserDetailsService

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefix cho các topic mà client sẽ subscribe để nhận message từ server
        // Ví dụ: /topic/consultation/123
        config.enableSimpleBroker("/topic");
        // Prefix cho các destination mà client sẽ gửi message đến server
        // Ví dụ: /app/chat.sendMessage
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint mà client sẽ kết nối WebSocket đến
        // "/ws" là endpoint, withSockJS() để hỗ trợ fallback nếu trình duyệt không hỗ
        // trợ WebSocket thuần
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null) {
                    StompCommand command = accessor.getCommand();
                    logger.info("STOMP Command: {}", command);
                    logger.info("STOMP Headers: {}", accessor.toNativeHeaderMap());

                    if (StompCommand.CONNECT.equals(command)) {
                        String authHeaderRaw = accessor.getFirstNativeHeader("Authorization");
                        logger.info("Raw Authorization header from STOMP CONNECT: {}", authHeaderRaw);

                        if (authHeaderRaw != null) {
                            String authHeader = authHeaderRaw.trim(); // Loại bỏ khoảng trắng thừa
                            if (authHeader.startsWith("Bearer ")) {
                                String jwt = authHeader.substring(7);
                                try {
                                    if (jwtUtils.validateJwtToken(jwt)) { // Sử dụng jwtUtils
                                        String username = jwtUtils.getPhoneNumberFromJwtToken(jwt); // Sử dụng jwtUtils
                                                                                                    // và giả định
                                                                                                    // phương thức này
                                                                                                    // trả về
                                                                                                    // username/phonenumber
                                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                                userDetails, null, userDetails.getAuthorities());

                                        accessor.setUser(authentication);
                                        logger.info("STOMP Connect: User {} authenticated successfully.", username);
                                    } else {
                                        logger.warn(
                                                "STOMP Connect: Invalid JWT token after trim. Original header: '{}', Trimmed: '{}'",
                                                authHeaderRaw, authHeader);
                                    }
                                } catch (Exception e) {
                                    logger.error("STOMP Connect: JWT token processing error: {}", e.getMessage(), e);
                                }
                            } else {
                                logger.warn(
                                        "STOMP Connect: Authorization token does not start with Bearer after trim. Original header: '{}', Trimmed: '{}'",
                                        authHeaderRaw, authHeader);
                            }
                        } else {
                            logger.warn("STOMP Connect: No Authorization header found.");
                        }
                    }
                }
                return message;
            }
        });
    }
}