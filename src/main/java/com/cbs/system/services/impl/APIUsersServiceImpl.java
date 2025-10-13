package com.cbs.system.services.impl;

import com.cbs.system.dto.APIUserDTO;
import com.cbs.system.entity.APIUsers;
import com.cbs.system.repository.APIUsersRepository;
import com.cbs.system.services.APIUsersService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class APIUsersServiceImpl implements APIUsersService {

    private final APIUsersRepository repo;
    private final PasswordEncoder passwordEncoder;

    public APIUsersServiceImpl(APIUsersRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<APIUserDTO> list(String q, Pageable pageable) {
        Page<APIUsers> page = (q == null || q.isBlank())
                ? repo.findAll(pageable)
                : repo.findByUsernameContainingIgnoreCaseOrAllowedIpContainingIgnoreCase(q, q, pageable);
        return page.map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public APIUserDTO get(Long id) {
        APIUsers u = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("API user not found: " + id));
        return toDTO(u);
    }

    @Override
    public APIUserDTO create(APIUserDTO dto) {
        if (repo.existsByUsername(dto.getUsername()))
            throw new DataIntegrityViolationException("Username already exists");

        APIUsers u = new APIUsers();
        u.setUsername(dto.getUsername());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setActive(dto.isActive());
        u.setAllowedIp(dto.getAllowedIp());
        return toDTO(repo.save(u));
    }

    @Override
    public APIUserDTO update(Long id, APIUserDTO dto) {
        APIUsers u = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("API user not found: " + id));

        if (!u.getUsername().equals(dto.getUsername()) && repo.existsByUsername(dto.getUsername()))
            throw new DataIntegrityViolationException("Username already exists");

        u.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setActive(dto.isActive());
        u.setAllowedIp(dto.getAllowedIp());
        return toDTO(repo.save(u));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new EntityNotFoundException("API user not found: " + id);
        repo.deleteById(id);
    }

    private APIUserDTO toDTO(APIUsers u) {
        APIUserDTO d = new APIUserDTO();
        d.setId(u.getId());
        d.setUsername(u.getUsername());
        d.setActive(u.isActive());
        d.setAllowedIp(u.getAllowedIp());
        return d;
    }
}
