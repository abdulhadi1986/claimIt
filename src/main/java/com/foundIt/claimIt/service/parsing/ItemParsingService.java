package com.foundIt.claimIt.service.parsing;

import com.foundIt.claimIt.domain.entity.ItemEntity;
import com.foundIt.claimIt.exception.InvalidUserInputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemParsingService {

    private final TextExtractorFactory textExtractorFactory;

    public List<ItemEntity> parseLostAndFoundItems(MultipartFile file) {
        try {
            TextExtractor textExtractor = textExtractorFactory.getExtractor(
                    Objects.requireNonNull(file.getOriginalFilename()));
            String extractedText = textExtractor.extractText(file);
            return extractItems(extractedText);
        } catch (IOException e) {
            log.error("IOException while parsing file [{}]", e.getMessage(), e);
            throw new RuntimeException("Failed to process the uploaded file", e);
        }
    }

    /**
     * Parses the given text and maps it into ItemEntity object.
     * The process is order agnostic which means that contents order doesn't matter
     * These 3 contents will result in the same object :
     * | ItemName: phone    | Quantity: 3       | Place: station
     * | Quantity: 3        | ItemName: phone   | Quantity: 3
     * | Place: station     | Place: station    | ItemName: phone
     * -----------------------------------------------------------
     * Values that don't represent this data will be ignored
     * @param text
     * @return List<ItemEntity>
     * @throws InvalidUserInputException when one of the 3 properties is missing
     */
    private List<ItemEntity> extractItems(String text) {
        List<ItemEntity> items = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return items;
        }

        ItemEntity currentItem = null;
        String[] lines = text.split("\\R");

        for (String rawLine : lines) {
            // If current ItemEntity is fully mapped then add it to the list and reset it.
            if (currentItem != null && currentItem.getName() != null && currentItem.getPlace() != null &&
                currentItem.getQuantity() != null) {
                items.add(currentItem);
                currentItem = null;
            }

            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("ItemName:") || line.startsWith("itemName:")) {
                //If current ItemEntity is initialized, it means we already iterated through the loop and some data is already filled
                if (currentItem != null) {
                    //Treat an ItemName line as the start of a new item record. but also
                    //Allow a missing ItemName to be filled later ONLY when the item already contains other identifying data (Place or Quantity).
                    if (currentItem.getName() == null && (currentItem.getPlace() != null || currentItem.getQuantity() != null)) {
                        String value = cleanValue(line.substring(line.indexOf(':') + 1).trim());
                        if (!value.isEmpty()) {
                            currentItem.setName(value);
                        }
                        continue;
                    } else {
                        //Reject the file if a new ItemName is encountered while the previous item is still in an inconsistent state.
                        log.error("Error while parsing uploaded file: Missing Quantity OR Place data in one of the entries");
                        throw new InvalidUserInputException("Missing Quantity OR Place data in one of the entries");
                    }
                }
                //Otherwise, we start initializing new ItemEntity
                currentItem = new ItemEntity();
                String value = cleanValue(line.substring(line.indexOf(':') + 1).trim());
                if (!value.isEmpty()) {
                    currentItem.setName(value);
                }
                continue;
            }

            if (line.startsWith("Quantity:") || line.startsWith("quantity:")) {
                if (currentItem != null) {
                    if (currentItem.getQuantity() == null && (currentItem.getPlace() != null || currentItem.getName() != null)) {
                        String value = cleanValue(line.substring(line.indexOf(':') + 1).trim());
                        if (!value.isEmpty()) {
                            currentItem.setQuantity((long) parseQuantity(value));
                        }
                        continue;
                    } else {
                        log.error("Error while parsing uploaded file: Missing Name OR Place data in one of the entries");
                        throw new InvalidUserInputException("Missing Name OR Place data in one of the entries");
                    }
                }
                currentItem = new ItemEntity();
                String value = cleanValue(line.substring(line.indexOf(':') + 1).trim());
                if (!value.isEmpty()) {
                    currentItem.setQuantity((long) parseQuantity(value));
                }
                continue;
            }

            if (line.startsWith("Place:") || line.startsWith("place:")) {
                if (currentItem != null) {
                    if (currentItem.getPlace() == null && (currentItem.getQuantity() != null || currentItem.getName() != null)) {
                        String value = cleanValue(line.substring(line.indexOf(':') + 1).trim());
                        if (!value.isEmpty()) {
                            currentItem.setPlace(value);
                        }
                        continue;
                    } else {
                        log.error("Error while parsing uploaded file: Missing Name OR Quantity data in one of the entries");
                        throw new InvalidUserInputException("Missing Name OR Quantity data in one of the entries");
                    }
                }
                currentItem = new ItemEntity();
                String value = cleanValue(line.substring(line.indexOf(':') + 1).trim());
                if (!value.isEmpty()) {
                    currentItem.setPlace(value);
                }
            }
        }
        //Validate latest ItemEntity for consistency
        if (currentItem != null && currentItem.getName() != null && currentItem.getQuantity() != null &&
            currentItem.getPlace() != null) {
            items.add(currentItem);
        }

        if(currentItem != null && (currentItem.getName() == null || currentItem.getQuantity() == null ||
           currentItem.getPlace() == null)) {
            log.error("Error while parsing uploaded file: Missing required data in one of the entries");
            throw new InvalidUserInputException("Missing required data in one of the entries");
        }

        return items;
    }

    private int parseQuantity(String raw) {
        if (raw == null) {
            return 1;
        }
        try {
            String digits = raw.replaceAll("\\D+", "");
            if (!digits.isEmpty()) {
                int q = Integer.parseInt(digits);
                return Math.max(1, q);
            }
        } catch (NumberFormatException ignored) {
        }
        return 1;
    }

    private String cleanValue(String val) {
        if (val == null) {
            return null;
        }
        // remove surrounding quotes/backticks
        String cleaned = val.replaceAll("^[\"'`]+|[\"'`]+$", "");
        return cleaned.replaceAll("[^A-Za-z0-9\\-_.:,() ]+", "");
    }
}
