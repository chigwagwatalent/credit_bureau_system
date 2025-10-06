package com.cbs.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_request_audit")
public class ApiRequestAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username", length = 100)
    private String username; // API username resolved from JWT

    @Column(name="path", length = 255, nullable = false)
    private String path;

    @Column(name="http_method", length = 10, nullable = false)
    private String httpMethod;

    @Column(name="cdb_xd", length = 64, nullable = false)
    private String cdbXd;

    @Column(name="eds_xd", length = 64, nullable = false)
    private String edsXd;

    @Column(name="authorization", length = 512, nullable = false)
    private String authorization;

    @Column(name="client_ip", length = 64)
    private String clientIp;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ApiRequestAudit() {}

    public ApiRequestAudit(String username, String path, String httpMethod,
                           String cdbXd, String edsXd, String authorization, String clientIp) {
        this.username = username;
        this.path = path;
        this.httpMethod = httpMethod;
        this.cdbXd = cdbXd;
        this.edsXd = edsXd;
        this.authorization = authorization;
        this.clientIp = clientIp;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPath() { return path; }
    public String getHttpMethod() { return httpMethod; }
    public String getCdbXd() { return cdbXd; }
    public String getEdsXd() { return edsXd; }
    public String getAuthorization() { return authorization; }
    public String getClientIp() { return clientIp; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setUsername(String username) { this.username = username; }
    public void setPath(String path) { this.path = path; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    public void setCdbXd(String cdbXd) { this.cdbXd = cdbXd; }
    public void setEdsXd(String edsXd) { this.edsXd = edsXd; }
    public void setAuthorization(String authorization) { this.authorization = authorization; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
}
