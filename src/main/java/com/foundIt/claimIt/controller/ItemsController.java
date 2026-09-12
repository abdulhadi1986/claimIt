package com.foundIt.claimIt.controller;

import com.foundIt.claimIt.domain.model.FileUploadResponse;
import com.foundIt.claimIt.domain.model.Item;
import com.foundIt.claimIt.domain.model.ItemResponse;
import com.foundIt.claimIt.service.ItemsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Items", description = "Endpoints for uploading and retrieving lost-and-found items")
public class ItemsController {
    private final ItemsService itemsService;

    @Operation(
            summary = "Upload lost-and-found items file",
            description = "Uploads a text file that contains records for item descriptions, quantity, and location. Requires a Bearer token in the Authorization header with role ADMIN."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "File accepted and processed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FileUploadResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid uploaded file", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/claimit/items-mgt/items-uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(type = "string", format = "binary")))
            @RequestPart("file") MultipartFile file) {
        log.info("Request received to upload lost items [{}]", file.getOriginalFilename());
        List<Item> uploadedItems = itemsService.processUploadedLostAndFoundItems(file);
        log.info("Successfully uploaded [{}] lost items from file [{}]", uploadedItems.size(), file.getOriginalFilename());
        return ResponseEntity.status(202).body(FileUploadResponse.builder().numberOfItems(uploadedItems.size()).build());
    }

    @Operation(
            summary = "Get all lost and found items",
            description = "Returns the currently stored list of lost-and-found items."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping(value = "/claimit/items/lost-and-found-items", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemResponse> getLostItems() {
        return ResponseEntity.ok(ItemResponse.builder()
                .itemList(itemsService.getAllLostAndFoundItems())
                .build());
    }
}
