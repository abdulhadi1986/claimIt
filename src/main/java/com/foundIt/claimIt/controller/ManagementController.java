package com.foundIt.claimIt.controller;

import com.foundIt.claimIt.domain.model.Item;
import com.foundIt.claimIt.service.LostAndFoundItemsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ManagementController {
    private final LostAndFoundItemsService lostAndFoundItemsService;

    @PostMapping(value = "/management/found-items-uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        log.info("Request received to upload lost items [{}]", file.getOriginalFilename());
        List<Item> uploadedItems = lostAndFoundItemsService.processUploadedLostAndFoundItems(file);
        log.info("Successfully uploaded [{}] lost items from file [{}]", uploadedItems.size(), file.getOriginalFilename());
        return ResponseEntity.status(202).body(uploadedItems.size() + " Items uploaded and processed successfully");
    }}
