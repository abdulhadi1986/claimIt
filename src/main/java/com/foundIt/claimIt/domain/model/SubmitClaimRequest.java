package com.foundIt.claimIt.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitClaimRequest {
    @NotNull
    @JsonProperty("itemId")
    private String itemId;
    @NotNull
    @JsonProperty("quantity")
    private Long quantity;
}
