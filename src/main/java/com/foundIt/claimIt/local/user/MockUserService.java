package com.foundIt.claimIt.local.user;

import com.foundIt.claimIt.local.domain.LocalUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MockUserService {

    public Optional<LocalUser> getUserData(String id) {
        String userName = id.substring(id.lastIndexOf('-') + 1);
        return Optional.of(LocalUser.builder().userId(id).userName(userName).build());
    }
}
