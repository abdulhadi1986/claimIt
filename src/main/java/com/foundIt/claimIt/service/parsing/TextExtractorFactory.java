package com.foundIt.claimIt.service.parsing;

import com.foundIt.claimIt.domain.type.FileType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TextExtractorFactory {

    private final List<TextExtractor> extractors;

    public TextExtractorFactory(List<TextExtractor> extractors) {
        this.extractors = extractors;
    }

    public TextExtractor getExtractor(String fileName) {
        FileType fileType = FileType.getFileTypeFromExtension(fileName.substring(fileName.lastIndexOf(".")));
        return extractors.stream()
                .filter(extractor -> extractor.supportsFileType(fileType))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No extractor found for " + fileType));
    }
}
