package com.cbs.system.restcontroller;

import com.cbs.system.entity.APIUsers;
import com.cbs.system.repository.APIUsersRepository;
import com.cbs.system.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

record ApiLoginRequest(String username, String password) {}
record ApiLoginResponse(String token) {}

@RestController
@RequestMapping
public class ApiAuthRestController {

    private static final Logger log = LoggerFactory.getLogger(ApiAuthRestController.class);

    private final APIUsersRepository apiUsersRepository;
    private final PasswordEncoder passwordEncoder; 
    private final JwtService jwtService;

    public ApiAuthRestController(APIUsersRepository apiUsersRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService) {
        this.apiUsersRepository = apiUsersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/datum/login")
    public ResponseEntity<?> apiLogin(@RequestBody ApiLoginRequest req, HttpServletRequest httpReq) {
        final String requestId = UUID.randomUUID().toString();
        final String clientIp = clientIp(httpReq);
        final String userAgent = httpReq.getHeader("User-Agent");

        try {
            if (req == null || isBlank(req.username()) || isBlank(req.password())) {
                log.warn("LOGIN_FAIL requestId={} ip={} reason=missing_fields ua={}", requestId, clientIp, safe(userAgent));
                return ResponseEntity.status(400).body("username and password are required");
            }

            Optional<APIUsers> userOpt = apiUsersRepository.findByUsername(req.username());
            if (userOpt.isEmpty()) {
                log.warn("LOGIN_FAIL requestId={} ip={} username={} reason=user_not_found ua={}",
                        requestId, clientIp, req.username(), safe(userAgent));
                return ResponseEntity.status(401).body("Invalid credentials");
            }

            APIUsers user = userOpt.get();
            if (!user.isActive()) {
                log.warn("LOGIN_FAIL requestId={} ip={} username={} reason=user_inactive ua={}",
                        requestId, clientIp, user.getUsername(), safe(userAgent));
                return ResponseEntity.status(401).body("Invalid credentials");
            }
            
            if (!passwordEncoder.matches(req.password(), user.getPassword())) {
                log.warn("LOGIN_FAIL requestId={} ip={} username={} reason=bad_password ua={}",
                        requestId, clientIp, user.getUsername(), safe(userAgent));
                return ResponseEntity.status(401).body("Invalid credentials");
            }

            String token = jwtService.generate(user.getUsername());
            log.info("LOGIN_OK requestId={} ip={} username={} ua={}",
                    requestId, clientIp, user.getUsername(), safe(userAgent));

            return ResponseEntity.ok(new ApiLoginResponse(token));
        } catch (Exception ex) {
            log.error("LOGIN_ERROR requestId={} ip={} username={} msg={}",
                    requestId, clientIp, req != null ? req.username() : "null", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().body("Login error");
        }
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    private static String clientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) return xf.split(",")[0].trim();
        return request.getRemoteAddr();
    }
    private static String safe(String s) {
        if (s == null) return "null";        
        return s.length() > 200 ? s.substring(0, 200) + "…" : s;
    }
}
