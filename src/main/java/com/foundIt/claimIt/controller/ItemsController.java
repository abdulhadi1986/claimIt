package com.foundIt.claimIt.controller;

import com.foundIt.claimIt.domain.model.Item;
import com.foundIt.claimIt.domain.model.ItemResponse;
import com.foundIt.claimIt.service.ItemsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class ItemsController {
    private final ItemsService itemsService;

    @PostMapping(value = "/claimit/items-mgt/items-uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        log.info("Request received to upload lost items [{}]", file.getOriginalFilename());
        List<Item> uploadedItems = itemsService.processUploadedLostAndFoundItems(file);
        log.info("Successfully uploaded [{}] lost items from file [{}]", uploadedItems.size(), file.getOriginalFilename());
        return ResponseEntity.status(202).body(uploadedItems.size() + " Items uploaded and processed successfully");
    }

    @GetMapping(value = "/claimit/items/lost-and-found-items", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemResponse> getLostItems() {
        return ResponseEntity.ok(ItemResponse.builder()
                .itemList(itemsService.getAllLostAndFoundItems())
                .build());
    }
}
