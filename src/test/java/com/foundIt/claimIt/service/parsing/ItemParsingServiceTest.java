package com.foundIt.claimIt.service.parsing;

import com.foundIt.claimIt.exception.InvalidUserInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.foundIt.claimIt.domain.entity.ItemEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ItemParsingServiceTest {
    private static final String TEST_FILES_PATH = "test-files/";
    private static final String VALID_PDF_FILE = "valid.pdf";
    private static final String VALID_WORD_FILE = "valid.docx";
    private static final String VALID_TEXT_FILE = "valid.txt";
    private static final String INVALID_FILE_CONTENT_1 = "invalid-missing-values.txt";
    private static final String INVALID_FILE_CONTENT_2 = "invalid-missing-strange-chars.txt";
    private static final String INVALID_FILE_EXTENSION = "invalid-extension.xml";

    private final ItemParsingService itemParsingService = constructService();

    @Test
    @DisplayName("Test: When uploading valid PDF file return successful response")
    void parseLostAndFoundItems_PDF_success() throws Exception {
        java.net.URL resource = getClass().getClassLoader().getResource(TEST_FILES_PATH + VALID_PDF_FILE);
        if (resource == null) throw new IllegalArgumentException("Resource not found: "+ VALID_PDF_FILE);
        MultipartFile multipartFile = getMultipartFile(resource, VALID_PDF_FILE, "application/pdf");

        List<ItemEntity> items = itemParsingService.parseLostAndFoundItems(multipartFile);

        assertNotNull(items, "Parsed items should not be null");
        assertEquals(2, items.size(), "Expect 2 items parsed from ItemName.pdf");
    }

    @Test
    @DisplayName("Test: When uploading valid WORD file return successful response")
    void parseLostAndFoundItems_WORD_success() throws Exception {
        java.net.URL resource = getClass().getClassLoader().getResource(TEST_FILES_PATH + VALID_WORD_FILE);
        if (resource == null) throw new IllegalArgumentException("Resource not found: "+ VALID_WORD_FILE);
        MultipartFile multipartFile = getMultipartFile(resource, VALID_WORD_FILE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        List<ItemEntity> items = itemParsingService.parseLostAndFoundItems(multipartFile);

        assertNotNull(items, "Parsed items should not be null");
        assertEquals(2, items.size(), "Expect 2 items parsed from ItemName.pdf");
    }

    @Test
    @DisplayName("Test: When uploading valid TXT file with shuffled order return successful response")
    void parseLostAndFoundItems_TXT_success() throws Exception {
        java.net.URL resource = getClass().getClassLoader().getResource(TEST_FILES_PATH + VALID_TEXT_FILE);
        if (resource == null) throw new IllegalArgumentException("Resource not found: "+ VALID_TEXT_FILE);
        MultipartFile multipartFile = getMultipartFile(resource, VALID_TEXT_FILE, "text/plain");

        List<ItemEntity> items = itemParsingService.parseLostAndFoundItems(multipartFile);

        assertNotNull(items, "Parsed items should not be null");
        assertEquals(3, items.size(), "Expect 2 items parsed from ItemName.pdf");
    }

    @Test
    @DisplayName("Test: When uploading missing values TXT file return error")
    void parseLostAndFoundItems_TXT_error() throws Exception {
        java.net.URL resource = getClass().getClassLoader().getResource(TEST_FILES_PATH + INVALID_FILE_CONTENT_1);
        if (resource == null) throw new IllegalArgumentException("Resource not found: "+ INVALID_FILE_CONTENT_1);
        MultipartFile multipartFile = getMultipartFile(resource, INVALID_FILE_CONTENT_1, "text/plain");

        assertThrows(InvalidUserInputException.class, ()-> itemParsingService.parseLostAndFoundItems(multipartFile));
    }

    @Test
    @DisplayName("Test: When uploading invalid characters TXT file return error")
    void parseLostAndFoundItems_TXT_error_2() throws Exception {
        java.net.URL resource = getClass().getClassLoader().getResource(TEST_FILES_PATH + INVALID_FILE_CONTENT_2);
        if (resource == null) throw new IllegalArgumentException("Resource not found: "+ INVALID_FILE_CONTENT_2);
        MultipartFile multipartFile = getMultipartFile(resource, INVALID_FILE_CONTENT_2, "text/plain");

        assertThrows(InvalidUserInputException.class, ()-> itemParsingService.parseLostAndFoundItems(multipartFile));
    }

    @Test
    @DisplayName("Test: When uploading unsupported file type return error")
    void parseLostAndFoundItems_XML_error() throws Exception {
        java.net.URL resource = getClass().getClassLoader().getResource(TEST_FILES_PATH + INVALID_FILE_EXTENSION);
        if (resource == null) throw new IllegalArgumentException("Resource not found: "+ INVALID_FILE_EXTENSION);
        MultipartFile multipartFile = getMultipartFile(resource, INVALID_FILE_EXTENSION, "application/xml");

        assertThrows(InvalidUserInputException.class, ()-> itemParsingService.parseLostAndFoundItems(multipartFile));
    }

    @Test
    @DisplayName("Test: When uploading invalid contents TXT file return error")
    void parseLostAndFoundItems_TXT_error_3() throws Exception {
        java.net.URL resource = getClass().getClassLoader().getResource(TEST_FILES_PATH + "empty.txt");
        if (resource == null) throw new IllegalArgumentException("Resource not found: "+ "empty.txt");
        MultipartFile multipartFile = getMultipartFile(resource, "empty.txt", "text/plain");

        List<ItemEntity> items = itemParsingService.parseLostAndFoundItems(multipartFile);

        assertNotNull(items, "Parsed items should not be null");
        assertThat(items).isEmpty();
    }

    private MultipartFile getMultipartFile(URL resource, String fileName, String fileType) throws URISyntaxException, IOException {
        Path path = Path.of(resource.toURI());
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile(fileName, fileName, fileType, content);
    }

    private ItemParsingService constructService() {

        PdfTextExtractor pdfTextExtractor = new PdfTextExtractor();
        MsWordTextExtractor msWordTextExtractor = new MsWordTextExtractor();
        PlainTextExtractor plainTextExtractor = new PlainTextExtractor();

        return new ItemParsingService(
                new TextExtractorFactory(List.of(pdfTextExtractor, msWordTextExtractor, plainTextExtractor)));
    }

}