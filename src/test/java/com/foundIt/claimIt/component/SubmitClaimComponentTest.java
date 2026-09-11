package com.foundIt.claimIt.component;

import com.foundIt.claimIt.domain.entity.ItemEntity;
import com.foundIt.claimIt.repository.ClaimRepository;
import com.foundIt.claimIt.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;

@SpringBootTest
public class SubmitClaimComponentTest {
    @Autowired
    private ClaimRepository claimRepository;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setup() {
        claimRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("CT: error response when submit claim request is null")
    void submitClaim_invalid_request_null() {
        given()
                .header("content-type", "application/json")
                .expect()
                .statusCode(500)
                .when()
                .post("http://localhost:8080/claimit/claims/claim-submissions")
                .then();
    }

    @Test
    @DisplayName("CT: error response when submit claim request missing quantity")
    void submitClaim_invalid_request_missingItme() {
        String requestBody = String.format("""
                {
                "itemId": "%s"
                }""", 1);

        given().body(requestBody)
                .header("content-type", "application/json")
                .expect()
                .statusCode(400)
                .when()
                .post("http://localhost:8080/claimit/claims/claim-submissions")
                .then();
    }

    @Test
    @DisplayName("CT: error response when submit claim request missing item")
    void submitClaim_invalid_request_missingQty() {
        String requestBody = """
                {
                "quantity": 1
                }""";

        given().body(requestBody)
                .header("content-type", "application/json")
                .expect()
                .statusCode(400)
                .when()
                .post("http://localhost:8080/claimit/claims/claim-submissions")
                .then();
    }

    @Test
    @DisplayName("CT: successful response when submit claim for item")
    void submitClaim_success() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setName("Laptop");
        itemEntity.setPlace("Train Station");
        itemEntity.setQuantity(1L);
        var savedId = itemRepository.save(itemEntity).getId();
        String requestBody = String.format("""
                {
                "itemId": "%s",
                "quantity": 1
                }""", savedId);

        given().body(requestBody)
                .header("content-type", "application/json")
                .expect()
                .statusCode(202)
                .when()
                .post("http://localhost:8080/claimit/claims/claim-submissions")
                .then();
    }

    @Test
    @DisplayName("CT: error response when submit claim for none existing item")
    void submitClaim_ItemNotFoundError() {
        String requestBody = String.format("""
                {
                "itemId": "%s",
                "quantity": 1
                }""", 100);

        given().body(requestBody)
                .header("content-type", "application/json")
                .expect()
                .statusCode(400)
                .when()
                .post("http://localhost:8080/claimit/claims/claim-submissions")
                .then();
    }

    @Test
    @DisplayName("CT: error response when submit claim for Quantity more than registered")
    void submitClaim_incorrectQtyError() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setName("Laptop");
        itemEntity.setPlace("Train Station");
        itemEntity.setQuantity(1L);
        var savedId = itemRepository.save(itemEntity).getId();
        String requestBody = String.format("""
                {
                "itemId": "%s",
                "quantity": 3
                }""", savedId);

        given().body(requestBody)
                .header("content-type", "application/json")
                .expect()
                .statusCode(400)
                .when()
                .post("http://localhost:8080/claimit/claims/claim-submissions")
                .then();
    }
}
