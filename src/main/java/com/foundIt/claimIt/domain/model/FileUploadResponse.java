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
@Schema(name = "FileUploadResponse", description = "Response returned after a file upload is processed.")
public class FileUploadResponse {
    @JsonProperty("numberOfUploadedItems")
    @Schema(description = "Number of items extracted from the uploaded file", example = "3")
    private int numberOfItems;
}
