package com.cbs.system.repository;

import com.cbs.system.entity.APIUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface APIUsersRepository extends JpaRepository<APIUsers, Long> {
    Optional<APIUsers> findByUsername(String username);
    long countByActiveTrue();
    
 
    boolean existsByUsername(String username);

    Page<APIUsers> findByUsernameContainingIgnoreCaseOrAllowedIpContainingIgnoreCase(
            String username, String allowedIp, Pageable pageable
    );
}
