// src/main/java/com/cbs/system/security/ApiHeadersAuthFilter.java
package com.cbs.system.security;

import com.cbs.system.entity.APIUsers;
import com.cbs.system.entity.ApiRequestAudit;
import com.cbs.system.repository.APIUsersRepository;
import com.cbs.system.repository.ApiRequestAuditRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ApiHeadersAuthFilter extends OncePerRequestFilter {

    private static final String API_PREFIX = "/v1/api/";
    private static final String HDR_AUTH = HttpHeaders.AUTHORIZATION; // "Authorization"
    private static final String HDR_CDB = "CDB-XD";
    private static final String HDR_EDS = "EDS-XD";

    private final JwtService jwtService;
    private final APIUsersRepository apiUsersRepository;
    private final ApiRequestAuditRepository auditRepository;

    public ApiHeadersAuthFilter(JwtService jwtService,
                                APIUsersRepository apiUsersRepository,
                                ApiRequestAuditRepository auditRepository) {
        this.jwtService = jwtService;
        this.apiUsersRepository = apiUsersRepository;
        this.auditRepository = auditRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Filter only /v1/api/** (context-path aware)
        String path = request.getRequestURI();
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }
        return !path.startsWith(API_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String cdbXd = request.getHeader(HDR_CDB);
        String edsXd = request.getHeader(HDR_EDS);
        String authorization = request.getHeader(HDR_AUTH);

        if (cdbXd == null || cdbXd.isBlank() ||
            edsXd == null || edsXd.isBlank() ||
            authorization == null || !authorization.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing required headers: CDB-XD, EDS-XD, Authorization");
            return;
        }

        if (!isUuid(cdbXd) || !isUuid(edsXd)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("CDB-XD and EDS-XD must be valid UUIDs");
            return;
        }

        String token = authorization.substring("Bearer ".length()).trim();
        final String username;
        try {
            username = jwtService.validateAndGetSubject(token);
        } catch (JwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        Optional<APIUsers> apiUserOpt = apiUsersRepository.findByUsername(username);
        if (apiUserOpt.isEmpty() || !apiUserOpt.get().isActive()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("API user not active or not found");
            return;
        }

        String clientIp = request.getRemoteAddr();
        APIUsers apiUser = apiUserOpt.get();
        if (apiUser.getAllowedIp() != null && !apiUser.getAllowedIp().isBlank()) {
            if (!apiUser.getAllowedIp().equals(clientIp)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("IP not allowed");
                return;
            }
        }

        auditRepository.save(new ApiRequestAudit(
                username,
                request.getRequestURI(),
                request.getMethod(),
                cdbXd, edsXd, authorization,
                clientIp
        ));

        var auth = new AbstractAuthenticationToken(List.of(new SimpleGrantedAuthority("ROLE_API"))) {
            @Override public Object getCredentials() { return token; }
            @Override public Object getPrincipal() { return username; }
            @Override public boolean isAuthenticated() { return true; }
        };
        auth.setAuthenticated(true);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }

    private boolean isUuid(String s) {
        try { UUID.fromString(s); return true; }
        catch (IllegalArgumentException ex) { return false; }
    }
}
