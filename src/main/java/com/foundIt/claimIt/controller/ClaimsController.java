package com.foundIt.claimIt.controller;

import com.foundIt.claimIt.domain.model.Claim;
import com.foundIt.claimIt.domain.model.GetClaimsResponse;
import com.foundIt.claimIt.domain.model.SubmitClaimRequest;
import com.foundIt.claimIt.service.ClaimsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Claims", description = "Endpoints for submitting and retrieving claims")
public class ClaimsController {

    private final ClaimsService claimsService;

    @Operation(
            summary = "Submit a claim",
            description = "Creates a new claim record for a lost-and-found item."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Claim accepted and registered",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = Claim.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input payload", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping(value = "/claimit/claims/claim-submissions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Claim> submitClaim(@RequestBody @Valid SubmitClaimRequest submitClaimRequest) {
        log.info("Received request to submit claim request for item [{}] qty [{}]", submitClaimRequest.getItemId(), submitClaimRequest.getQuantity());
        Claim savedClaim = claimsService.processClaimRequest(submitClaimRequest.getItemId(), submitClaimRequest.getQuantity());
        return ResponseEntity.status(202).body(savedClaim);
    }

    @Operation(
            summary = "Get submitted claims",
            description = "Returns all submitted claims in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Claims retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GetClaimsResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping(value = "/claimit/claims/submitted-claims", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetClaimsResponse> getClaims() {
        return ResponseEntity.ok(GetClaimsResponse.builder().claims(claimsService.getSubmittedClaims()).build());
    }
}
