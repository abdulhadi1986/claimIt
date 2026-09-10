package com.foundIt.claimIt.service;

import com.foundIt.claimIt.domain.model.Item;
import com.foundIt.claimIt.domain.entity.ItemEntity;
import com.foundIt.claimIt.exception.InvalidUserInputException;
import com.foundIt.claimIt.mapper.DomainMapper;
import com.foundIt.claimIt.service.parsing.ItemParsingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LostAndFoundItemsService {

    private final ItemParsingService itemParsingService;
    private final StorageManagementService storageManagementService;

    public List<Item> processUploadedLostAndFoundItems(MultipartFile uploadedLostAndFoundItemsFile) {
        List<ItemEntity> entityList = itemParsingService.parseLostAndFoundItems(uploadedLostAndFoundItemsFile);
        if (entityList == null || entityList.isEmpty()) {
            log.error("Error while parsing uploaded file: No Contents are parsed");
            throw new InvalidUserInputException("Lost and Found Items could not be parsed from file " +
                                                uploadedLostAndFoundItemsFile.getOriginalFilename());
        }

        storageManagementService.saveLostAndFoundItemsToDB(entityList);
        return entityList.stream().map(DomainMapper.mapper()::mapToItem).toList();
    }

    public List<Item> getAllLostAndFoundItems() {
        List<ItemEntity> itemEntityList = storageManagementService.getAllItemsFromDB();
        if (itemEntityList == null || itemEntityList.isEmpty()) {
            log.warn("No Items found in the Database");
            return List.of();
        }
        return itemEntityList.stream().map(DomainMapper.mapper()::mapToItem).toList();
    }
}
