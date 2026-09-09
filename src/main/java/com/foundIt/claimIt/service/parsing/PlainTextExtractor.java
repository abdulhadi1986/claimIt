package com.foundIt.claimIt.service.parsing;

import com.foundIt.claimIt.domain.type.FileType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PlainTextExtractor implements TextExtractor {

    @Override
    public boolean supportsFileType(FileType fileType) {
        return FileType.TXT == fileType;
    }

    @Override
    public String extractText(MultipartFile file) throws IOException {
        String extractedText = new String(file.getInputStream().readAllBytes());
        return normalizeText(extractedText);
    }
}
