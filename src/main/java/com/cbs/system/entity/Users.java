// src/main/java/com/cbs/system/entity/Users.java
package com.cbs.system.entity;

import com.cbs.system.enums.RoleEnums;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username", nullable=false, unique=true, length=100)
    private String username;

    @Column(name="email", nullable=false, unique=true, length=150)
    private String email;

    @Column(name="password", nullable=false, length=255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable=false, length=50)
    private RoleEnums role;

    @Column(name="active", nullable=false)
    private boolean active = true;

    public Users() {}

    public Users(String username, String email, String password, RoleEnums role, boolean active) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { 
        this.username = username; }

    public String getEmail() { 
        return email; }
    public void setEmail(String email) { 
        this.email = email; }

    public String getPassword() { 
        return password; }
    public void setPassword(String password) { 
        this.password = password; }

    public RoleEnums getRole() { 
        return role; }
    
    public void setRole(RoleEnums role) { 
        this.role = role; }

    public boolean isActive() { 
        return active; }
    
    public void setActive(boolean active) { 
        this.active = active; }
}
