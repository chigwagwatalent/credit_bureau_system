package com.cbs.system.services;

import com.cbs.system.dto.CivilianDataDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CivilianDataServices {

    Page<CivilianDataDTO> list(String q, Pageable pageable);

    CivilianDataDTO get(Long id);

    CivilianDataDTO create(CivilianDataDTO dto);

    CivilianDataDTO update(Long id, CivilianDataDTO dto);

    void delete(Long id);
}
