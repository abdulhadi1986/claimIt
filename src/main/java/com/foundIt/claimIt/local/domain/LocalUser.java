package com.foundIt.claimIt.local.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocalUser {
    private String userId;
    private String userName;
}
