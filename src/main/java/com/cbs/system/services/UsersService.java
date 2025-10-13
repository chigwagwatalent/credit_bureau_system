package com.cbs.system.services;

import com.cbs.system.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsersService {
    Page<UserDTO> list(String q, Pageable pageable);
    UserDTO get(Long id);
    UserDTO create(UserDTO dto);
    UserDTO update(Long id, UserDTO dto);
    void delete(Long id);
}
