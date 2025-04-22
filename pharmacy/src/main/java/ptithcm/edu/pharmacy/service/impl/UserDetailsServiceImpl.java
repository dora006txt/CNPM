package ptithcm.edu.pharmacy.service.impl;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional
import ptithcm.edu.pharmacy.entity.User;
import ptithcm.edu.pharmacy.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional // Add @Transactional annotation to manage the session
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with phone number: " + phoneNumber));

        // Explicitly initialize the roles collection while the session is active
        Hibernate.initialize(user.getRoles());
        // Alternatively, you could trigger initialization by accessing the collection:
        // user.getRoles().size();

        // Map roles to Spring Security GrantedAuthority objects
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName())) // Adjust if your Role entity structure is different (e.g., role.getName())
                .collect(Collectors.toList());

        // Return the UserDetails object expected by Spring Security
        // Include account status checks (enabled, etc.) based on your User entity
        return new org.springframework.security.core.userdetails.User(
                user.getPhoneNumber(), // Principal (username)
                user.getPasswordHash(), // Password
                user.getIsActive(), // enabled <--- Add this
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities); // Authorities (roles)
    }
}