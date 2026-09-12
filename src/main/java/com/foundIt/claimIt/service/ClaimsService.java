package com.foundIt.claimIt.service;

import com.foundIt.claimIt.domain.entity.ClaimEntity;
import com.foundIt.claimIt.domain.entity.ItemEntity;
import com.foundIt.claimIt.domain.entity.UserEntity;
import com.foundIt.claimIt.domain.model.Claim;
import com.foundIt.claimIt.exception.AuthenticationException;
import com.foundIt.claimIt.exception.InvalidUserInputException;
import com.foundIt.claimIt.local.user.MockUserService;
import com.foundIt.claimIt.local.user.UserAuthEntity;
import com.foundIt.claimIt.mapper.DomainMapper;
import com.foundIt.claimIt.repository.ClaimRepository;
import com.foundIt.claimIt.repository.ItemRepository;
import com.foundIt.claimIt.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimsService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final MockUserService mockUserService;

    public Claim processClaimRequest(@NotNull String itemId, @NotNull Long qty) {
        //get user data from userService
        UserAuthEntity userData = mockUserService.getUserData().orElseThrow(() ->{
            log.error("Authenticated UserData not found");
            return new AuthenticationException("Authenticated UserData not found");
        });

        ItemEntity itemEntity = itemRepository.findById(Long.valueOf(itemId)).orElseThrow(() -> {
            log.error("Item with id {} not found", itemId);
            return new InvalidUserInputException("No Item found for the given request");
        });

        if (itemEntity.getQuantity() < qty) {
            log.error("Item with id {} has [{}] less than requested quantity [{}]",
                    itemId,
                    itemEntity.getQuantity(),
                    qty);
            throw new InvalidUserInputException("Claimed quantity is more than the registered items available");
        }

        ClaimEntity claimEntity = saveClaimToDB(qty, itemEntity, userData);
        return DomainMapper.mapper().mapToClaim(claimEntity);
    }

    public List<Claim> getSubmittedClaims() {
        return claimRepository.findAll().stream()
                .map(DomainMapper.mapper()::mapToClaim)
                .toList();
    }

    private ClaimEntity saveClaimToDB(Long qty, ItemEntity itemEntity, UserAuthEntity userData) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userData.getId());
        userEntity.setUserName(userData.getUserName());
        if (!userRepository.existsById(userEntity.getId())) {
            userRepository.save(userEntity);
        }

        ClaimEntity claimEntity = new ClaimEntity();
        claimEntity.setQuantity(qty);
        claimEntity.setItemEntity(itemEntity);
        claimEntity.setUserEntity(userEntity);
        return claimRepository.save(claimEntity);
    }
}
