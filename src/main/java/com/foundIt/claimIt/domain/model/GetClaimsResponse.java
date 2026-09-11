package com.foundIt.claimIt.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "GetClaimsResponse", description = "Response containing all submitted claims.")
public class GetClaimsResponse {
    @JsonProperty("submittedClaims")
    @Schema(description = "List of submitted claims", implementation = Claim.class)
    List<Claim> claims;
}
