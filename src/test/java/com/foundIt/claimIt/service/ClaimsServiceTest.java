package com.foundIt.claimIt.service;

import com.foundIt.claimIt.domain.entity.ClaimEntity;
import com.foundIt.claimIt.domain.entity.ItemEntity;
import com.foundIt.claimIt.domain.entity.UserEntity;
import com.foundIt.claimIt.domain.model.Claim;
import com.foundIt.claimIt.exception.AuthenticationException;
import com.foundIt.claimIt.exception.InvalidUserInputException;
import com.foundIt.claimIt.local.user.MockUserService;
import com.foundIt.claimIt.local.user.UserAuthEntity;
import com.foundIt.claimIt.repository.ClaimRepository;
import com.foundIt.claimIt.repository.ItemRepository;
import com.foundIt.claimIt.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimsServiceTest {
    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MockUserService userService;

    @Captor
    private ArgumentCaptor<ClaimEntity> claimEntityArgumentCaptor;

    @Captor
    private ArgumentCaptor<UserEntity> userEntityArgumentCaptor;

    @InjectMocks
    ClaimsService claimsService;

    @Test
    @DisplayName("UT: successful when register claim")
    void processClaimRequest() {
        UserAuthEntity userAuthEntity = new UserAuthEntity();
        userAuthEntity.setId("user-id");
        userAuthEntity.setUserName("user-name");
        when(userService.getUserData()).thenReturn(Optional.of(userAuthEntity));
        ItemEntity itemEntity = getItemEntity(1986L, 2L);

        when(itemRepository.findById(1986L)).thenReturn(Optional.of(itemEntity));
        when(userRepository.existsById(anyString())).thenReturn(false);
        when(claimRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        claimsService.processClaimRequest("1986", 2L);

        verify(claimRepository).save(claimEntityArgumentCaptor.capture());

        UserEntity expectedUserEntity = new UserEntity();
        expectedUserEntity.setId(userAuthEntity.getId());
        expectedUserEntity.setUserName(userAuthEntity.getUserName());

        var actualClaimEntity = claimEntityArgumentCaptor.getValue();
        assertThat(actualClaimEntity.getUserEntity().getId()).isEqualTo(expectedUserEntity.getId());
        assertThat(actualClaimEntity.getUserEntity().getUserName()).isEqualTo(expectedUserEntity.getUserName());
        assertThat(actualClaimEntity.getItemEntity().getId()).isEqualTo(itemEntity.getId());

        verify(userRepository).save(userEntityArgumentCaptor.capture());
        var actualUserEntity = userEntityArgumentCaptor.getValue();
        assertThat(actualUserEntity.getId()).isEqualTo(expectedUserEntity.getId());
        assertThat(actualUserEntity.getUserName()).isEqualTo(expectedUserEntity.getUserName());
    }

    @Test
    @DisplayName("UT: error user not found when register claim")
    void processClaimRequest_userNotFoundError() {
        when(userService.getUserData()).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class, () ->claimsService.processClaimRequest("1986", 2L));
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(claimRepository);
    }

    @Test
    @DisplayName("UT: error item not found when register claim")
    void processClaimRequest_itemNotFoundError() {
        UserAuthEntity userAuthEntity = new UserAuthEntity();
        userAuthEntity.setId("user-id");
        userAuthEntity.setUserName("user-name");
        when(userService.getUserData()).thenReturn(Optional.of(userAuthEntity));

        when(itemRepository.findById(1986L)).thenReturn(Optional.empty());

        assertThrows(InvalidUserInputException.class, () ->claimsService.processClaimRequest("1986", 2L));
        verifyNoInteractions(userRepository);
        verifyNoInteractions(claimRepository);
    }

    @Test
    @DisplayName("UT: error invalid qty when register claim and claimed qty is more than the registered record")
    void processClaimRequest_qtyInputError() {
        UserAuthEntity userAuthEntity = new UserAuthEntity();
        userAuthEntity.setId("user-id");
        userAuthEntity.setUserName("user-name");
        when(userService.getUserData()).thenReturn(Optional.of(userAuthEntity));
        ItemEntity itemEntity = getItemEntity(1986L, 1L);

        when(itemRepository.findById(1986L)).thenReturn(Optional.of(itemEntity));
        assertThrows(InvalidUserInputException.class, () ->claimsService.processClaimRequest("1986", 2L));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(claimRepository);
    }

    @Test
    @DisplayName("UT: success when get all submitted claims")
    void getClaims_success() {
        ClaimEntity claimEntity1 = getClaimEntity(1L, 1L);
        when(claimRepository.findAll()).thenReturn(List.of(claimEntity1));
        List<Claim> returnedClaims = claimsService.getSubmittedClaims();
        assertNotNull(returnedClaims);
    }

    private ClaimEntity getClaimEntity(Long id, Long qty) {
        ClaimEntity claimEntity1 = new ClaimEntity();
        claimEntity1.setId(id);
        claimEntity1.setQuantity(qty);
        claimEntity1.setItemEntity(getItemEntity(1L, 2L));
        UserEntity userEntity1 = new UserEntity();
        userEntity1.setId("100");
        userEntity1.setUserName("userName1");
        claimEntity1.setUserEntity(userEntity1);
        claimEntity1.setCreatedAt(Instant.now());
        return claimEntity1;
    }

    private ItemEntity getItemEntity(long id, long quantity) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(id);
        itemEntity.setName("Laptop");
        itemEntity.setQuantity(quantity);
        itemEntity.setPlace("Train Station");
        return itemEntity;
    }
}