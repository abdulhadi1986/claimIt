package com.foundIt.claimIt.mapper;

import com.foundIt.claimIt.domain.model.Item;
import com.foundIt.claimIt.domain.entity.ItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomainMapper {
    static DomainMapper mapper() {
        return Mappers.getMapper(DomainMapper.class);
    }

    Item mapToItem(ItemEntity itemEntity);
}
