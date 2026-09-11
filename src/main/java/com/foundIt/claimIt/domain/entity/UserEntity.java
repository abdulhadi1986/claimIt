package com.foundIt.claimIt.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {
    @Id
    @NotEmpty
    private String id;

    @Column(name = "userName")
    @NotEmpty(message = "Name is required")
    private String userName;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ClaimEntity> claimEntities = new ArrayList<>();
}
