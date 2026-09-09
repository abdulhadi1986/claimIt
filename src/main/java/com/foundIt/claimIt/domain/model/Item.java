package com.foundIt.claimIt.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    private Long id;
    private String name;
    private String place;
    private Long quantity;
}
