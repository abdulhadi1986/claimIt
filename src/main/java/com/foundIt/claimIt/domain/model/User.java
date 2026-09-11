package com.foundIt.claimIt.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @JsonProperty("userId")
    private String id;
    @JsonProperty("userName")
    private String userName;
}
