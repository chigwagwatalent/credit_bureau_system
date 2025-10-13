package com.cbs.system.dto;

import jakarta.validation.constraints.*;

public class APIUserDTO {
    private Long id;

    @NotBlank @Size(max = 100)
    private String username;

    @Size(min = 6, max = 255, message = "Password must be at least 6 chars")
    private String password; // blank on edit to keep unchanged

    private boolean active = true;

    @Size(max = 64)
    private String allowedIp;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getAllowedIp() { return allowedIp; }
    public void setAllowedIp(String allowedIp) { this.allowedIp = allowedIp; }
}
