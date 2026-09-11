package com.foundIt.claimIt.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Claim", description = "Claim submitted against a found item.")
public class Claim {
    @JsonProperty("claimedItem")
    @Schema(description = "The item being claimed")
    private Item item;

    @JsonProperty("submittedBy")
    @Schema(description = "The user who submitted the claim")
    private User user;

    @JsonProperty("claimedQuantity")
    @Schema(description = "Quantity claimed by the user", example = "1")
    private Long quantity;

    @JsonProperty("submittedAt")
    @Schema(description = "When the claim was submitted", example = "2026-09-11")
    private LocalDate createdAt;
}
