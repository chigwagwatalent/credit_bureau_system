package com.cbs.system.dto;

import com.cbs.system.enums.RoleEnums;
import jakarta.validation.constraints.*;

public class UserDTO {
    private Long id;

    @NotBlank @Size(max = 100)
    private String username;

    @NotBlank @Email @Size(max = 150)
    private String email;

    @Size(min = 6, max = 255, message = "Password must be at least 6 characters")
    private String password; // blank on edit to keep unchanged

    @NotNull
    private RoleEnums role;

    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public RoleEnums getRole() { return role; }
    public void setRole(RoleEnums role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
