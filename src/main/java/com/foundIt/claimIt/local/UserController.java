package com.foundIt.claimIt.local;

import com.foundIt.claimIt.local.user.MockAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {
    private final MockAuthenticationService authenticationService;

    @PostMapping(value = "/login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> login(@RequestPart String userName, @RequestPart String password) {
        log.debug("Login attempt for user {}", userName);
        return ResponseEntity.ok(authenticationService.generateToken(userName, password));
    }
}
