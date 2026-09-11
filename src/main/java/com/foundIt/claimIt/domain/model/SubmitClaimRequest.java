package com.foundIt.claimIt.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SubmitClaimRequest", description = "Request payload used to submit a claim for an item.")
public class SubmitClaimRequest {
    @NotNull
    @JsonProperty("itemId")
    @Schema(description = "Identifier of the lost-and-found item being claimed", example = "123")
    private String itemId;

    @NotNull
    @JsonProperty("quantity")
    @Schema(description = "Quantity being claimed for the item", example = "1")
    private Long quantity;
}
