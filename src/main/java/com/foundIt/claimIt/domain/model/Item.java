package com.foundIt.claimIt.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Item", description = "Lost-and-found item information.")
public class Item {
    @JsonProperty("itemId")
    @Schema(description = "Unique identifier of the item", example = "1")
    private Long id;

    @JsonProperty("description")
    @Schema(description = "Description or name of the item", example = "Laptop")
    private String name;

    @JsonProperty("foundAt")
    @Schema(description = "Location where the item was found", example = "Airport")
    private String place;

    @JsonProperty("quantity")
    @Schema(description = "Available quantity of the item", example = "2")
    private Long quantity;
}
