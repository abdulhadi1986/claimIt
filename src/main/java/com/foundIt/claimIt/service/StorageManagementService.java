package com.foundIt.claimIt.service;

import com.foundIt.claimIt.domain.entity.ItemEntity;
import com.foundIt.claimIt.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageManagementService {

    private final ItemRepository itemRepository;

    public void saveLostAndFoundItemsToDB(List<ItemEntity> itemEntityList) {
        log.info("Saving LostANDFound Items to the Database");
        for (ItemEntity itemEntity : itemEntityList) {
            Optional<ItemEntity> optionalExistingItem =
                    itemRepository.findByNameAndPlace(itemEntity.getName(),itemEntity.getPlace());
            if (optionalExistingItem.isPresent()) {
                var existingItem = optionalExistingItem.get();
                log.info("Found [{}] similar record(s) in the DB. Updating quantity", existingItem.getQuantity());
                itemEntity.setId(existingItem.getId());
                itemEntity.setName(existingItem.getName());
                itemEntity.setPlace(existingItem.getPlace());
                itemEntity.setQuantity(existingItem.getQuantity() + itemEntity.getQuantity());
            }
        }
        itemRepository.saveAll(itemEntityList);
    }
}
