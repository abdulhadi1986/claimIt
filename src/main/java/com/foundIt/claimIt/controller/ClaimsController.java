package com.foundIt.claimIt.controller;

import com.foundIt.claimIt.domain.model.GetClaimsResponse;
import com.foundIt.claimIt.domain.model.SubmitClaimRequest;
import com.foundIt.claimIt.service.ClaimsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<String> submitClaim(@RequestBody @Valid SubmitClaimRequest submitClaimRequest) {
        log.info("Received request to submit claim request for item [{}] qty [{}]", submitClaimRequest.getItemId(), submitClaimRequest.getQuantity());
        Long id = claimsService.processClaimRequest(submitClaimRequest.getItemId(), submitClaimRequest.getQuantity());
        return ResponseEntity.status(202).body("Claim for item [{}] is registered with id : [" + id + "]");
    }

    @GetMapping(value = "/claimit/claims/submitted-claims", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetClaimsResponse> getClaims() {
        return ResponseEntity.ok(GetClaimsResponse.builder().claims(claimsService.getSubmittedClaims()).build());
    }
}
