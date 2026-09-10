package com.foundIt.claimIt.component;

import com.foundIt.claimIt.domain.entity.ItemEntity;
import com.foundIt.claimIt.domain.model.ItemResponse;
import com.foundIt.claimIt.mapper.DomainMapper;
import com.foundIt.claimIt.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class GetItemsComponentTest {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("CT: when get lost and found items successful response")
    void uploadFile_success() {
        List<ItemEntity> savedItems = saveToDatabase();

        var actualResponse = given()
                .expect()
                .statusCode(200)
                .when()
                .get("http://localhost:8080/claimit/items/lost-and-found-items")
                .as(ItemResponse.class);
        var expectedItems = savedItems.stream().map(DomainMapper.mapper()::mapToItem).toList();
        assertThat(actualResponse).isEqualTo(ItemResponse.builder().itemList(expectedItems).build());
    }

    private List<ItemEntity> saveToDatabase() {
        ItemEntity laptop = new ItemEntity();
        laptop.setName("Laptop");
        laptop.setPlace("Taxi");
        laptop.setQuantity(1L);

        ItemEntity headphones = new ItemEntity();
        headphones.setName("Headphones");
        headphones.setPlace("Railway station");
        headphones.setQuantity(2L);

        ItemEntity phone = new ItemEntity();
        phone.setName("Mobile Phone");
        phone.setPlace("Airport");
        phone.setQuantity(3L);

        return itemRepository.saveAll(List.of(laptop, headphones, phone));
    }
}
