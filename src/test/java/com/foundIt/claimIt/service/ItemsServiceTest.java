package com.foundIt.claimIt.service;

import com.foundIt.claimIt.domain.entity.ItemEntity;
import com.foundIt.claimIt.domain.model.Item;
import com.foundIt.claimIt.exception.InvalidUserInputException;
import com.foundIt.claimIt.repository.ItemRepository;
import com.foundIt.claimIt.service.parsing.ItemParsingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemsServiceTest {
    @Mock
    private ItemParsingService itemParsingService;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemsService itemsService;

    @Test
    @DisplayName("UT: when processing uploaded file happy flow")
    void processLostAndFoundItems_success() {
        ItemEntity itemEntity  = new ItemEntity();
        itemEntity.setId(12345L);
        itemEntity.setName("Phone");
        itemEntity.setPlace("Train Station");
        when(itemParsingService.parseLostAndFoundItems(any())).thenReturn(List.of(itemEntity));

        doNothing().when(itemRepository).saveLostAndFoundItemsToDB(any());

        var returnedResponse = itemsService.processUploadedLostAndFoundItems(new MockMultipartFile("file-name.pdf", "file-contents".getBytes()));
        assertNotNull(returnedResponse);
        assertThat(returnedResponse).hasSize(1);
        Item returnedITem = returnedResponse.getFirst();
        assertThat(returnedITem.getName()).isEqualTo(itemEntity.getName());
        assertThat(returnedITem.getPlace()).isEqualTo(itemEntity.getPlace());
        assertThat(returnedITem.getId()).isEqualTo(itemEntity.getId());

        verify(itemRepository).saveLostAndFoundItemsToDB(List.of(itemEntity));
    }

    @Test
    @DisplayName("UT: when processing uploaded file returns empty item list throws exception")
    void processLostAndFoundItems_exception() {
        when(itemParsingService.parseLostAndFoundItems(any())).thenReturn(List.of());
        assertThrows(InvalidUserInputException.class, ()-> itemsService.processUploadedLostAndFoundItems(new MockMultipartFile("file-name.pdf", "file-contents".getBytes())));
        verifyNoInteractions(itemRepository);
    }
}