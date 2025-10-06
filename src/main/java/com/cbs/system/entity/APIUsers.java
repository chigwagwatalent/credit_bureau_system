package com.cbs.system.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "api_users")
public class APIUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name="password", nullable = false, length = 255)
    private String password; // BCrypt

    @Column(name="active", nullable = false)
    private boolean active = true;

    @Column(name="allowed_ip", length = 64)
    private String allowedIp;

    public APIUsers() {}

    public APIUsers(String username, String password, boolean active, String allowedIp) {
        this.username = username;
        this.password = password;
        this.active = active;
        this.allowedIp = allowedIp;
    }

    public Long getId() { 
        return id; }
    public String getUsername() { 
        return username; }
    public void setUsername(String username) { 
        this.username = username; }
    public String getPassword() { 
        return password; }
    public void setPassword(String password) { 
        this.password = password; }
    public boolean isActive() { 
        return active; }
    public void setActive(boolean active) { 
        this.active = active; }
    public String getAllowedIp() { 
        return allowedIp; }
    public void setAllowedIp(String allowedIp) { 
        this.allowedIp = allowedIp; }
}
