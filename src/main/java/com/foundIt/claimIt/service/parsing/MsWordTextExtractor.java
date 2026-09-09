package com.foundIt.claimIt.service.parsing;

import com.foundIt.claimIt.domain.type.FileType;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class MsWordTextExtractor implements TextExtractor {

    @Override
    public boolean supportsFileType(FileType fileType) {
        return FileType.DOCX == fileType;
    }

    @Override
    public String extractText(MultipartFile file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream());
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            String extractedText = extractor.getText();
            return normalizeText(extractedText);
        }
    }
}
