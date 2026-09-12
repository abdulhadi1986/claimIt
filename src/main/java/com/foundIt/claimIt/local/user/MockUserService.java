package com.foundIt.claimIt.local.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MockUserService {
    private final UserAuthRepository userAuthRepository;

    public Optional<UserAuthEntity> getUserData() {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                        .getName();
        return userAuthRepository.findByEmail(username);
    }
}
