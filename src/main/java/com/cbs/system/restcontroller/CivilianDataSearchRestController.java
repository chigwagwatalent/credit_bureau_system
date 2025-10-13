// src/main/java/com/cbs/system/restcontroller/CivilianDataSearchRestController.java
package com.cbs.system.restcontroller;

import com.cbs.system.dto.CivilianDataDTO;
import com.cbs.system.entity.APIUsers;
import com.cbs.system.entity.CivilianData;
import com.cbs.system.mapper.CivilianDataMapper;
import com.cbs.system.repository.APIUsersRepository;
import com.cbs.system.repository.CivilianDataRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/api/civilians")
@Validated
public class CivilianDataSearchRestController {

    private final APIUsersRepository apiUsersRepository;
    private final CivilianDataRepository civilianDataRepository;
    private final PasswordEncoder passwordEncoder;

    public CivilianDataSearchRestController(APIUsersRepository apiUsersRepository,
                                            CivilianDataRepository civilianDataRepository,
                                            PasswordEncoder passwordEncoder) {
        this.apiUsersRepository = apiUsersRepository;
        this.civilianDataRepository = civilianDataRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/lookup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> lookup(@RequestBody(required = false) LookupRequest req,
                                    @RequestHeader(value = "X-API-USER", required = false) String hdrUser,
                                    @RequestHeader(value = "X-API-PASS", required = false) String hdrPass,
                                    HttpServletRequest httpReq) {

        String username = (req != null && req.username() != null && !req.username().isBlank())
                ? req.username() : hdrUser;
        String password = (req != null && req.password() != null) ? req.password() : hdrPass;
        String nationalId = (req != null) ? req.nationalId() : null;

        if (username == null || password == null || nationalId == null || nationalId.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.of("username, password, nationalId required"));
        }

        Optional<APIUsers> apiUserOpt = apiUsersRepository.findByUsername(username);
        if (apiUserOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiError.of("Invalid credentials"));

        APIUsers apiUser = apiUserOpt.get();
        if (!apiUser.isActive()) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiError.of("Account disabled"));
        if (!passwordEncoder.matches(password, apiUser.getPassword()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiError.of("Invalid credentials"));

        String clientIp = clientIp(httpReq);
        String allowed = apiUser.getAllowedIp();
        if (allowed != null && !allowed.isBlank()) {
            boolean permitted = false;
            for (String token : allowed.split(",")) {
                if (clientIp.equals(token.trim())) { permitted = true; break; }
            }
            if (!permitted) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiError.of("IP not allowed"));
        }

        Optional<CivilianData> civOpt = civilianDataRepository.findByNationalId(nationalId);
        if (civOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.of("Civilian not found"));

        CivilianDataDTO dto = CivilianDataMapper.toDTO(civOpt.get());
        return ResponseEntity.ok(dto);
    }

    private String clientIp(HttpServletRequest request) {
        String h = request.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) {
            int comma = h.indexOf(',');
            return comma > 0 ? h.substring(0, comma).trim() : h.trim();
        }
        return request.getRemoteAddr();
    }

    public record LookupRequest(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String nationalId
    ) {}

    public record ApiError(String message) {
        public static ApiError of(String msg) { return new ApiError(msg); }
    }
}
