package com.foundIt.claimIt.controller;

import com.foundIt.claimIt.domain.model.ItemResponse;
import com.foundIt.claimIt.exception.InvalidUserInputException;
import com.foundIt.claimIt.service.ItemsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemsControllerTest {

    @Mock
    private ItemsService itemsService;

    @InjectMocks
    ItemsController itemsController;

    @Test
    @DisplayName("UT: Upload file API successful response")
    void uploadValidFile_success() {
        when(itemsService.processUploadedLostAndFoundItems(any())).thenReturn(List.of());
        var response = itemsController.uploadFile(getSampleFile());
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    @DisplayName("UT: Upload file API 400 Error")
    void uploadValidFile_400_Exception() {
        when(itemsService.processUploadedLostAndFoundItems(any())).thenThrow(
                new InvalidUserInputException("Invalid Input"));
        assertThrows(InvalidUserInputException.class, () -> itemsController.uploadFile(getSampleFile()));
    }

    @Test
    @DisplayName("UT: Get all items successful response")
    void getLostAndFoundItems_success() {
        when(itemsService.getAllLostAndFoundItems()).thenReturn(List.of());
        var response = itemsController.getLostItems();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(ItemResponse.builder().itemList(List.of()).build());
    }

    private MultipartFile getSampleFile() {
        return new MockMultipartFile("file",
                "test.txt",
                "text/plain",
                "sample-content".getBytes()
        );
    }

}