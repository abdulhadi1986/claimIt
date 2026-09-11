package com.foundIt.claimIt.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Claim {
    @JsonProperty("claimedItem")
    private Item item;
    @JsonProperty("submittedBy")
    private User user;
    @JsonProperty("claimedQuantity")
    private Long quantity;
    @JsonProperty("submittedAt")
    private LocalDate createdAt;
}
