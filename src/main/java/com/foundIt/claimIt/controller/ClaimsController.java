package com.foundIt.claimIt.controller;

import com.foundIt.claimIt.domain.model.ClaimRequest;
import com.foundIt.claimIt.service.ClaimsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ClaimsController {

    private final ClaimsService claimsService;

    @PostMapping(value = "/claimit/claims/claim-submissions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> submitClaim(@RequestBody @Valid ClaimRequest claimRequest) {
        log.info("Received request to submit claim request for item [{}] qty [{}]", claimRequest.getItemId(), claimRequest.getQuantity());
        Long id = claimsService.processClaimRequest(claimRequest.getItemId(), claimRequest.getQuantity());
        return ResponseEntity.status(202).body("Claim for item [{}] is registered with id : [" + id + "]");
    }
}
