package ptithcm.edu.pharmacy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ptithcm.edu.pharmacy.entity.User;
import ptithcm.edu.pharmacy.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // Try to find user by phone number first (primary login method)
        User user = userRepository.findByPhoneNumber(login)
                .orElseGet(() -> userRepository.findByEmail(login)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with phone/email: " + login)));

        // Update last login time
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Convert roles to Spring Security authorities
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getPhoneNumber(), // Use phone number as the username
                user.getPasswordHash(),
                user.getIsActive(),
                true, // account non-expired
                true, // credentials non-expired
                true, // account non-locked
                authorities
        );
    }
}