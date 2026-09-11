package com.foundIt.claimIt.mapper;

import com.foundIt.claimIt.domain.entity.ClaimEntity;
import com.foundIt.claimIt.domain.entity.UserEntity;
import com.foundIt.claimIt.domain.model.Claim;
import com.foundIt.claimIt.domain.model.Item;
import com.foundIt.claimIt.domain.entity.ItemEntity;
import com.foundIt.claimIt.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.Date;

@Mapper
public interface DomainMapper {
    static DomainMapper mapper() {
        return Mappers.getMapper(DomainMapper.class);
    }

    Item mapToItem(ItemEntity itemEntity);

    @Mapping(target = "item", source = "itemEntity")
    @Mapping(target = "user", source = "userEntity")
    Claim mapToClaim(ClaimEntity claimEntity);

    User mapToUser(UserEntity userEntity);

    default Date map(Instant instant) {
        return instant == null ? null : Date.from(instant);
    }
}
