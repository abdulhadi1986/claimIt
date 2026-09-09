package com.foundIt.claimIt.component;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static io.restassured.RestAssured.given;

@SpringJUnitConfig(initializers = ConfigDataApplicationContextInitializer.class)
class FileUploadComponentTest {

    @Test
    @DisplayName("CT: when uploading valid file get successful response")
    void uploadFile_success() {
        given().multiPart("file", "valid-file.txt", getValidContent().getBytes(), "text/plain")
                .expect()
                .statusCode(202)
                .when()
                .post("http://localhost:8080/management/found-items-uploads")
                .then();
    }

    @Test
    @DisplayName("CT: when uploading invalid file get 400 response")
    void uploadFile_invalidContent() {
        given().multiPart("file", "invalid-file.txt", getInvalidContents().getBytes(), "text/plain")
                .expect()
                .statusCode(400)
                .when()
                .post("http://localhost:8080/management/found-items-uploads");
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