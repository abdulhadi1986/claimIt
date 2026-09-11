package com.foundIt.claimIt.repository;

import com.foundIt.claimIt.domain.entity.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<ClaimEntity, Long> {

}
