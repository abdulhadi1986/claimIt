package com.foundIt.claimIt.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lost_and_fount_items")
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name is required")
    private String name;

    @NotNull(message = "Quantity is required")
    private Long quantity;

    @NotEmpty(message = "Place is required")
    private String place;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "last_modified_at")
    private Instant lastModifiedAt;

    @OneToMany(mappedBy = "itemEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ClaimEntity> claimEntities = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.lastModifiedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedAt = Instant.now();
    }
}
