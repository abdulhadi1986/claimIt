package com.foundIt.claimIt.component;

import com.foundIt.claimIt.domain.entity.ClaimEntity;
import com.foundIt.claimIt.domain.entity.ItemEntity;
import com.foundIt.claimIt.domain.entity.UserEntity;
import com.foundIt.claimIt.repository.ClaimRepository;
import com.foundIt.claimIt.repository.ItemRepository;
import com.foundIt.claimIt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
public class SubmitClaimComponentTest {
    @Autowired
    private ClaimRepository claimRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        claimRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
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

    @Test
    @DisplayName("CT: successful response when get all claim request")
    void getSubmittedClaims_success() {
        var userEntity1 = userRepository.save(getUserEntity("101", "userName101"));
        var itemEntity = itemRepository.save(getItemEntity(2));
        claimRepository.save(getClaimEntity(1L, userEntity1, itemEntity));
        var userEntity2 = userRepository.save(getUserEntity("102", "userName102"));
        claimRepository.save(getClaimEntity(1L, userEntity2, itemEntity));

        given().header("content-type", "application/json")
                .expect()
                .statusCode(200)
                .when()
                .get("http://localhost:8080/claimit/claims/submitted-claims")
                .then()
                .body("submittedClaims", hasSize(2))
                // First claim
                .body("submittedClaims[0].claimedItem.description", equalTo("Laptop"))
                .body("submittedClaims[0].claimedItem.foundAt", equalTo("Train Station"))
                .body("submittedClaims[0].claimedItem.quantity", equalTo(2))
                .body("submittedClaims[0].submittedBy.userName", equalTo("userName101"))
                .body("submittedClaims[0].claimedQuantity", equalTo(1))
                // Second claim
                .body("submittedClaims[1].claimedItem.description", equalTo("Laptop"))
                .body("submittedClaims[1].claimedItem.foundAt", equalTo("Train Station"))
                .body("submittedClaims[1].claimedItem.quantity", equalTo(2))
                .body("submittedClaims[1].submittedBy.userName", equalTo("userName102"))
                .body("submittedClaims[1].claimedQuantity", equalTo(1));
    }

    private ClaimEntity getClaimEntity(Long qty, UserEntity userEntity, ItemEntity itemEntity) {
        ClaimEntity claimEntity1 = new ClaimEntity();
        claimEntity1.setQuantity(qty);
        claimEntity1.setItemEntity(itemEntity);
        claimEntity1.setUserEntity(userEntity);
        claimEntity1.setCreatedAt(Instant.now());
        return claimEntity1;
    }

    private static UserEntity getUserEntity(String id, String userName) {
        UserEntity userEntity1 = new UserEntity();
        userEntity1.setUserName(userName);
        userEntity1.setId(id);
        return userEntity1;
    }

    private ItemEntity getItemEntity(long quantity) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setName("Laptop");
        itemEntity.setQuantity(quantity);
        itemEntity.setPlace("Train Station");
        return itemEntity;
    }
}
