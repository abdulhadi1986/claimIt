package com.foundIt.claimIt.service.parsing;

import com.foundIt.claimIt.domain.type.FileType;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PdfTextExtractor implements TextExtractor {

    @Override
    public boolean supportsFileType(FileType fileType) {
        return FileType.PDF == fileType;
    }

    /**
     * Extracts text from PDF bytes while preserving visual reading coordinates.
     */

    @Override
    public String extractText(MultipartFile file) throws IOException {
        byte[] bytes = file.getInputStream().readAllBytes();
        try (PDDocument document = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            // Crucial: sorts text chunks by their visual Y and X coordinates on the page
            stripper.setSortByPosition(true);
            String extractedText = stripper.getText(document);
            return normalizeText(extractedText);
        }
    }
}
