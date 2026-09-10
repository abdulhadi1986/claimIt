package com.foundIt.claimIt.component;

import com.foundIt.claimIt.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;

@SpringBootTest
class FileUploadComponentTest {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("CT: when uploading valid file get successful response")
    void uploadFile_success() {
        given().multiPart("file", "valid-file.txt", getValidContent().getBytes(), "text/plain")
                .expect()
                .statusCode(202)
                .when()
                .post("http://localhost:8080/claimit/management/found-items-uploads")
                .then();
    }

    @Test
    @DisplayName("CT: when uploading invalid file get 400 response")
    void uploadFile_invalidContent() {
        given().multiPart("file", "invalid-file.txt", getInvalidContents().getBytes(), "text/plain")
                .expect()
                .statusCode(400)
                .when()
                .post("http://localhost:8080/claimit/management/found-items-uploads");
    }

    private String getValidContent() {
        return """
                Quantity: 1
                ItemName: Laptop
                Place: Taxi
                ----------------------------
                Place: Railway station
                ItemName: Headphones
                Quantity: 2
                ----------------------------
                ItemName: Mobile Phone
                Place: Airport
                Quantity: 3
                """;
    }

    private String getInvalidContents() {
        return """
                ItemName: Laptop
                Quantity: 1
                Place: Taxi
                ItemName: Laptop
                Quantity: 1
                ------------------------------------
                ItemName: Headphones
                Quantity: 2
                Place: Railway station
                """;
    }

}