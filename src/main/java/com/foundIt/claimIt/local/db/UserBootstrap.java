package com.foundIt.claimIt.local.db;

import com.foundIt.claimIt.domain.type.Roles;
import com.foundIt.claimIt.local.user.UserAuthEntity;
import com.foundIt.claimIt.local.user.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class UserBootstrap {
    private final UserAuthRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void createAdminUser() {
        Stream.of("1", "2").forEach(s -> saveUserToDB(s, Roles.ROLE_ADMIN));
        Stream.of("1", "2", "3").forEach(s -> saveUserToDB(s, Roles.ROLE_USER));
    }

    private void saveUserToDB(String sequence, Roles role) {
        String id = role.name().toLowerCase(Locale.ROOT) + sequence;
        if (userRepository.findByEmail(id + "@claimit.com").isEmpty()) {
            UserAuthEntity userEntity = new UserAuthEntity();
            userEntity.setId(UUID.randomUUID().toString());
            userEntity.setName(id);
            userEntity.setEmail(id + "@claimit.com");
            userEntity.setUserName(id + "@claimit.com");
            userEntity.setPassword(passwordEncoder.encode(id + "123"));
            userEntity.setPhone("1234567890");
            userEntity.setRole(role);
            userRepository.save(userEntity);
        }
    }
}