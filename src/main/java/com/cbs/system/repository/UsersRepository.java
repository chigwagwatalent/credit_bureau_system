package com.cbs.system.repository;

import com.cbs.system.entity.Users;
import com.cbs.system.enums.RoleEnums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    long countByActiveTrue();
    long countByRole(RoleEnums role);

    @Query("select u.role as role, count(u) as total from Users u group by u.role")
    List<Object[]> countByRoleGrouped();
    

    Page<Users> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username, String email, Pageable pageable
    );

}
