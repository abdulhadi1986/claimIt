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

    private List<ItemEntity> extractItems(String text) {
        List<ItemEntity> items = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return items;
        }

        ItemEntity current = null;
        String[] lines = text.split("\\R");

        for (String rawLine : lines) {
            if (current != null && current.getName() != null && current.getPlace() != null &&
                current.getQuantity() != null) {
                items.add(current);
                current = null;
            }

            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("ItemName:")) {
                if (current != null) {
                    if (current.getName() == null && (current.getPlace() != null || current.getQuantity() != null)) {
                        String value = cleanValue(line.substring(line.indexOf(':') + 1).trim());
                        if (!value.isEmpty()) {
                            current.setName(value);
                        }
                        continue;
                    } else {
                        log.error("Error while parsing uploaded file: Missing ItemName data in one of the entries");
                        throw new InvalidUserInputException("Missing ItemName data in one of the entries");
                    }
                }
                current = new ItemEntity();
                String value = cleanValue(line.substring(line.indexOf(':') + 1).trim());
                if (!value.isEmpty()) {
                    current.setName(value);
                }
                continue;
            }

            if (line.startsWith("Quantity:")) {
                if (current != null) {
                    if (current.getQuantity() == null && (current.getPlace() != null || current.getName() != null)) {
                        String value = cleanValue(line.substring(line.indexOf(':') + 1).trim());
                        if (!value.isEmpty()) {
                            current.setQuantity((long) parseQuantity(value));
                        }
                        continue;
                    } else {
                        log.error("Error while parsing uploaded file: Missing Quantity data in one of the entries");
                        throw new InvalidUserInputException("Missing Quantity data in one of the entries");
                    }
                }
                current = new ItemEntity();
                String value = cleanValue(line.substring(line.indexOf(':') + 1).trim());
                if (!value.isEmpty()) {
                    current.setQuantity((long) parseQuantity(value));
                }
                continue;
            }

            if (line.startsWith("Place:")) {
                if (current != null) {
                    if (current.getPlace() == null && (current.getQuantity() != null || current.getName() != null)) {
                        String value = cleanValue(line.substring(line.indexOf(':') + 1).trim());
                        if (!value.isEmpty()) {
                            current.setPlace(value);
                        }
                        continue;
                    } else {
                        log.error("Error while parsing uploaded file: Missing Place data in one of the entries");
                        throw new RuntimeException("Missing Place data in one of the entries");
                    }
                }
                current = new ItemEntity();
                String value = cleanValue(line.substring(line.indexOf(':') + 1).trim());
                if (!value.isEmpty()) {
                    current.setPlace(value);
                }
            }
        }

        if (current != null && current.getName() != null && current.getQuantity() != null &&
            current.getPlace() != null) {
            items.add(current);
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
