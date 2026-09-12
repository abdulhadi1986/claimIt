package com.foundIt.claimIt.component;

import com.foundIt.claimIt.repository.ItemRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

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
                .header("Authorization", "Bearer " + getUserToken("admin"))
                .expect()
                .statusCode(202)
                .when()
                .post("http://localhost:8080/claimit/items-mgt/items-uploads")
                .then();
    }

    @Test
    @DisplayName("CT: Error unauthorized when using USER ROLE")
    void uploadFile_unauthorizedUser() {
        given().multiPart("file", "valid-file.txt", getValidContent().getBytes(), "text/plain")
                .header("Authorization", "Bearer " + getUserToken("user"))
                .expect()
                .statusCode(403)
                .when()
                .post("http://localhost:8080/claimit/items-mgt/items-uploads")
                .then();
    }

    @Test
    @DisplayName("CT: Error unauthorized when no token")
    void uploadFile_unauthorizedMissingToken() {
        given().multiPart("file", "valid-file.txt", getValidContent().getBytes(), "text/plain")
                .expect()
                .statusCode(403)
                .when()
                .post("http://localhost:8080/claimit/items-mgt/items-uploads")
                .then();
    }

    @Test
    @DisplayName("CT: when uploading invalid file get 400 response")
    void uploadFile_invalidContent() {
        given().header("Authorization", "Bearer " + getUserToken("admin"))
                .multiPart("file", "invalid-file.txt", getInvalidContents().getBytes(), "text/plain")
                .expect()
                .statusCode(400)
                .when()
                .post("http://localhost:8080/claimit/items-mgt/items-uploads");
    }

    @Test
    @DisplayName("CT: when uploading invalid file get 400 response")
    void uploadFile_invalidType() {
        given().header("Authorization", "Bearer " + getUserToken("admin"))
                .multiPart("file", "invalid-file.xml", getInvalidContents().getBytes(), "text/plain")
                .expect()
                .statusCode(400)
                .when()
                .post("http://localhost:8080/claimit/items-mgt/items-uploads")
                .then()
                .contentType(ContentType.TEXT)
                .body(equalTo(".xml FileType is not supported. Only (.pdf, .docx and .txt) are allowed"));
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

    private String getUserToken(String role) {
        return given()
                .multiPart("userName", String.format("role_%s1@claimit.com", role))
                .multiPart("password", String.format("role_%s1123", role))
                .post("http://localhost:8080/login")
                .then()
                .contentType(ContentType.TEXT)
                .extract()
                .asString();
    }
}