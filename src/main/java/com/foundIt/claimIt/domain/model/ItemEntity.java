package com.foundIt.claimIt.domain.model;

import lombok.Data;

@Data
public class ItemEntity {
    private Long id;
    private String name;
    private Long quantity;
    private String place;
}
