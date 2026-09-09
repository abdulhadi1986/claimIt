package com.foundIt.claimIt.controller;

import com.foundIt.claimIt.exception.InvalidUserInputException;
import com.foundIt.claimIt.service.LostAndFoundItemsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManagementControllerTest {

    @Mock
    private LostAndFoundItemsService lostAndFoundItemsService;

    @InjectMocks
    ManagementController managementController;

    @Test
    void uploadValidFile_success() {
        when(lostAndFoundItemsService.processUploadedLostAndFoundItems(any())).thenReturn(List.of());
        var response = managementController.uploadFile(getSampleFile());
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void uploadValidFile_400_Exception() {
        when(lostAndFoundItemsService.processUploadedLostAndFoundItems(any())).thenThrow(
                new InvalidUserInputException("Invalid Input"));
        assertThrows(InvalidUserInputException.class, () ->managementController.uploadFile(getSampleFile()));
    }

    private MultipartFile getSampleFile() {
        return new MockMultipartFile("file",
                "test.txt",
                "text/plain",
                "sample-content".getBytes()
        );
    }

}