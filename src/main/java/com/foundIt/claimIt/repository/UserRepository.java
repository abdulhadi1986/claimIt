package com.foundIt.claimIt.repository;

import com.foundIt.claimIt.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {

}
