package io.github.luicit.luisprojectscore.security;

import io.github.luicit.luisprojectscore.config.CoreProperties;
import io.github.luicit.luisprojectscore.domain.entity.BaseUserEntity;
import io.github.luicit.luisprojectscore.domain.repository.BaseUserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CoreProperties coreProperties;
    private final BaseUserRepository userRepository;

    public CustomUserDetailsService(CoreProperties coreProperties, BaseUserRepository userRepository) {
        this.coreProperties = coreProperties;
        this.userRepository = userRepository;
    }

    @NullMarked
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        BaseUserEntity user;

        if ("username".equalsIgnoreCase(coreProperties.getSecurity().getLoginIdentifier())) {
            user = userRepository.findByUsernameAndDeletedAtIsNull(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } else {
            user = userRepository.findByEmailAndDeletedAtIsNull(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }

        return new org.springframework.security.core.userdetails.User(
                identifier,
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                user.getLockedUntil() == null || user.getLockedUntil().isBefore(LocalDateTime.now()),
                Collections.emptyList()
        );
    }
}
