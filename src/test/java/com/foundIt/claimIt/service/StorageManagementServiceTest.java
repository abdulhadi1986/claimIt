package com.foundIt.claimIt.service;

import com.foundIt.claimIt.domain.entity.ItemEntity;
import com.foundIt.claimIt.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StorageManagementServiceTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private StorageManagementService storageManagementService;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("UT: saving items to DB, update qty of existing items where name and place are similar")
    void saveLostAndFoundItemsToDB_updateExistingRecords() {
        assertThat(itemRepository.findAll()).isEmpty();
        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setName("Iphone 13 pro-max");
        itemEntity1.setPlace("Trian station");
        itemEntity1.setQuantity(3L);

        itemRepository.save(itemEntity1);
        List<ItemEntity> itemEntityList = itemRepository.findAll();

        assertThat(itemEntityList).hasSize(1);
        var existingItem = itemRepository.findAll().getFirst();
        var createdTimestamp = existingItem.getCreatedAt();
        var updatedTimestamp = existingItem.getLastModifiedAt();

        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setName("Iphone 13 pro-max");
        itemEntity2.setPlace("Trian station");
        itemEntity2.setQuantity(1L);

        ItemEntity itemEntity3 = new ItemEntity();
        itemEntity3.setName("Laptop");
        itemEntity3.setPlace("Bus station");
        itemEntity3.setQuantity(2L);

        storageManagementService.saveLostAndFoundItemsToDB(List.of(itemEntity2, itemEntity3));
        assertThat(itemRepository.findAll()).hasSize(2);
        var updatedItem = itemRepository
                .findByNameAndPlace("Iphone 13 pro-max", "Trian station")
                .orElseThrow();
        assertThat(updatedItem.getQuantity()).isEqualTo(4L);
        assertEquals(createdTimestamp, updatedItem.getCreatedAt());
        assertThat(updatedItem.getLastModifiedAt()).isAfter(updatedTimestamp);
    }

    @Test
    @DisplayName("UT: when saving items to DB successful scenario")
    void saveLostAndFoundItemsToDB_NoExistingRecords() {
        assertThat(itemRepository.findAll()).hasSize(0);

        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setName("Iphone 13 pro-max");
        itemEntity2.setPlace("Trian station");
        itemEntity2.setQuantity(1L);

        ItemEntity itemEntity3 = new ItemEntity();
        itemEntity3.setName("Laptop");
        itemEntity3.setPlace("Bus station");
        itemEntity3.setQuantity(2L);

        storageManagementService.saveLostAndFoundItemsToDB(List.of(itemEntity2, itemEntity3));
    }
}