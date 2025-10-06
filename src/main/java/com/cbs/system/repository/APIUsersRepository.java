package com.cbs.system.repository;

import com.cbs.system.entity.APIUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface APIUsersRepository extends JpaRepository<APIUsers, Long> {
    Optional<APIUsers> findByUsername(String username);
    long countByActiveTrue();
}
