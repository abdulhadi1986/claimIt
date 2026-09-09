package com.foundIt.claimIt.service.parsing;

import com.foundIt.claimIt.domain.type.FileType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TextExtractor {
    boolean supportsFileType(FileType fileType);
    String extractText(MultipartFile file) throws IOException;

    /**
     * Normalizes line breaks, Unicode spaces, smart quotes, and typographical dashes.
     */
    default String normalizeText(String text) {
        if (text == null) return "";
        return text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                // Non-breaking spaces and ideographic spaces
                .replaceAll("[\\u00A0\\u202F\\u2007\\u3000]", " ")
                // Unicode dashes (en-dash, em-dash, figure dash, etc.) to standard hyphen
                .replaceAll("[\\u2010\\u2011\\u2012\\u2013\\u2014\\u2015\\uFE58\\uFE63\\uFF0D]", "-")
                // Curly quotes to straight quotes
                .replaceAll("[\\u2018\\u2019\\u201A\\u201B]", "'")
                .replaceAll("[\\u201C\\u201D\\u201E\\u201F]", "\"");
    }
}
