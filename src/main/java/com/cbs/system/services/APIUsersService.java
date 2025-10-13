package com.cbs.system.services;

import com.cbs.system.dto.APIUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface APIUsersService {
    Page<APIUserDTO> list(String q, Pageable pageable);
    APIUserDTO get(Long id);
    APIUserDTO create(APIUserDTO dto);
    APIUserDTO update(Long id, APIUserDTO dto);
    void delete(Long id);
}
