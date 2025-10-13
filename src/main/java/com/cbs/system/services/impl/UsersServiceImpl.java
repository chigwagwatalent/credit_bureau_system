package com.cbs.system.services.impl;

import com.cbs.system.dto.UserDTO;
import com.cbs.system.entity.Users;
import com.cbs.system.repository.UsersRepository;
import com.cbs.system.services.UsersService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsersServiceImpl implements UsersService {

    private final UsersRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UsersServiceImpl(UsersRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> list(String q, Pageable pageable) {
        Page<Users> page = (q == null || q.isBlank())
                ? repo.findAll(pageable)
                : repo.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, pageable);
        return page.map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO get(Long id) {
        Users u = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        return toDTO(u);
    }

    @Override
    public UserDTO create(UserDTO dto) {
        if (repo.existsByUsername(dto.getUsername())) throw new DataIntegrityViolationException("Username already exists");
        if (repo.existsByEmail(dto.getEmail())) throw new DataIntegrityViolationException("Email already exists");

        Users u = new Users();
        u.setUsername(dto.getUsername());
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setRole(dto.getRole());
        u.setActive(dto.isActive());
        return toDTO(repo.save(u));
    }

    @Override
    public UserDTO update(Long id, UserDTO dto) {
        Users u = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

        if (!u.getUsername().equals(dto.getUsername()) && repo.existsByUsername(dto.getUsername()))
            throw new DataIntegrityViolationException("Username already exists");
        if (!u.getEmail().equals(dto.getEmail()) && repo.existsByEmail(dto.getEmail()))
            throw new DataIntegrityViolationException("Email already exists");

        u.setUsername(dto.getUsername());
        u.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        u.setRole(dto.getRole());
        u.setActive(dto.isActive());
        return toDTO(repo.save(u));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new EntityNotFoundException("User not found: " + id);
        repo.deleteById(id);
    }

    private UserDTO toDTO(Users u) {
        UserDTO d = new UserDTO();
        d.setId(u.getId());
        d.setUsername(u.getUsername());
        d.setEmail(u.getEmail());
        d.setRole(u.getRole());
        d.setActive(u.isActive());
        return d;
    }
}
