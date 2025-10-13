package com.cbs.system.services.impl;

import com.cbs.system.dto.CivilianDataDTO;
import com.cbs.system.entity.CivilianData;
import com.cbs.system.mapper.CivilianDataMapper;
import com.cbs.system.repository.CivilianDataRepository;
import com.cbs.system.services.CivilianDataServices;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CivilianDataServicesImpl implements CivilianDataServices {

    private final CivilianDataRepository repo;

    public CivilianDataServicesImpl(CivilianDataRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CivilianDataDTO> list(String q, Pageable pageable) {
        Page<CivilianData> page;
        if (q == null || q.isBlank()) {
            page = repo.findAll(pageable);
        } else {
            page = repo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrNationalIdContainingIgnoreCase(
                    q, q, q, pageable
            );
        }
        return page.map(CivilianDataMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CivilianDataDTO get(Long id) {
        CivilianData e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Civilian not found: " + id));
        return CivilianDataMapper.toDTO(e);
    }

    @Override
    public CivilianDataDTO create(CivilianDataDTO dto) {
        if (dto.getEmail() != null && repo.existsByEmail(dto.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        if (repo.existsByNationalId(dto.getNationalId())) {
            throw new DataIntegrityViolationException("National ID already exists");
        }
        CivilianData saved = repo.save(CivilianDataMapper.toEntity(dto));
        return CivilianDataMapper.toDTO(saved);
    }

    @Override
    public CivilianDataDTO update(Long id, CivilianDataDTO dto) {
        CivilianData existing = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Civilian not found: " + id));

        if (dto.getEmail() != null) {
            repo.findByEmail(dto.getEmail())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(x -> { throw new DataIntegrityViolationException("Email already exists"); });
        }
        if (dto.getNationalId() != null) {
            repo.findByNationalId(dto.getNationalId())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(x -> { throw new DataIntegrityViolationException("National ID already exists"); });
        }

        CivilianDataMapper.copyToEntity(dto, existing);
        CivilianData saved = repo.save(existing);
        return CivilianDataMapper.toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Civilian not found: " + id);
        }
        repo.deleteById(id);
    }
}
