package com.foundIt.claimIt.service;

import com.foundIt.claimIt.domain.model.Item;
import com.foundIt.claimIt.domain.entity.ItemEntity;
import com.foundIt.claimIt.mapper.DomainMapper;
import com.foundIt.claimIt.service.parsing.ItemParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LostAndFoundItemsService {
    private final ItemParsingService itemParsingService;
    private final StorageManagementService storageManagementService;

    public List<Item> processUploadedLostAndFoundItems(MultipartFile uploadedLostAndFoundItemsFile) {
        List<ItemEntity> entityList = itemParsingService.parseLostAndFoundItems(uploadedLostAndFoundItemsFile);
        if (entityList == null || entityList.isEmpty()) {
            throw new RuntimeException("Lost and Found Items could not be parsed");
        }
        storageManagementService.saveLostAndFoundItemsToDB(entityList);
        return entityList.stream().map(DomainMapper.mapper()::mapToItem).toList();
    }
}
