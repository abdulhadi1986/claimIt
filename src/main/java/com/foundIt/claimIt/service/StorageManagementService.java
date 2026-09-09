package com.foundIt.claimIt.service;

import com.foundIt.claimIt.domain.entity.ItemEntity;
import com.foundIt.claimIt.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StorageManagementService {

    private final ItemRepository itemRepository;

    public void saveLostAndFoundItemsToDB(List<ItemEntity> itemEntityList) {
        for (ItemEntity itemEntity : itemEntityList) {
            Optional<ItemEntity> optionalExistingItems =
                    itemRepository.findByNameAndPlace(itemEntity.getName(),itemEntity.getPlace());
            if (optionalExistingItems.isPresent()) {
                var existingItems = optionalExistingItems.get();
                itemEntity.setId(existingItems.getId());
                itemEntity.setName(existingItems.getName());
                itemEntity.setPlace(existingItems.getPlace());
                itemEntity.setQuantity(existingItems.getQuantity() + itemEntity.getQuantity());
            }
        }
        itemRepository.saveAll(itemEntityList);
    }
}
