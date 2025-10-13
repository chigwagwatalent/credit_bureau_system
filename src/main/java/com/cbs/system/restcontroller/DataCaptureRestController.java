package com.cbs.system.restcontroller;

import com.cbs.system.dto.CivilianDataDTO;
import com.cbs.system.entity.APIUsers;
import com.cbs.system.entity.CivilianData;
import com.cbs.system.mapper.CivilianDataMapper;
import com.cbs.system.repository.APIUsersRepository;
import com.cbs.system.repository.CivilianDataRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/civilians")
@Validated
public class DataCaptureRestController {

    private final APIUsersRepository apiUsersRepository;
    private final CivilianDataRepository civilianDataRepository;
    private final PasswordEncoder passwordEncoder;

    public DataCaptureRestController(APIUsersRepository apiUsersRepository,
                                     CivilianDataRepository civilianDataRepository,
                                     PasswordEncoder passwordEncoder) {
        this.apiUsersRepository = apiUsersRepository;
        this.civilianDataRepository = civilianDataRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> create(@RequestBody(required = false) @Valid CreateRequest req,
                                    @RequestHeader(value = "X-API-USER", required = false) String hdrUser,
                                    @RequestHeader(value = "X-API-PASS", required = false) String hdrPass,
                                    HttpServletRequest httpReq) {

        String username = (req != null && notBlank(req.username)) ? req.username : hdrUser;
        String password = (req != null && req.password != null) ? req.password : hdrPass;
        if (!notBlank(username) || !notBlank(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.of("username and password required"));
        }
        if (req == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.of("request body required"));
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

        if (notBlank(req.email) && civilianDataRepository.existsByEmail(req.email))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of("Email already exists"));
        if (civilianDataRepository.existsByNationalId(req.nationalId))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of("National ID already exists"));

        CivilianData entity = new CivilianData(
                req.firstName,
                req.lastName,
                req.gender,
                req.dateOfBirth,
                req.nationalId,
                req.phone,
                req.email,
                req.address,
                req.education,
                req.employmentStatus,
                req.employer,
                req.monthlyIncome,
                req.maritalStatus,
                req.bloodType,
                req.allergies,
                req.bankName,
                req.accountNumber,
                req.creditScore
        );

        entity.setCreditCardsCount(req.creditCardsCount == null ? 0 : req.creditCardsCount);
        entity.setCurrentLoansCount(req.currentLoansCount == null ? 0 : req.currentLoansCount);

        CivilianData saved;
        try {
            saved = civilianDataRepository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of("Unique constraint violation"));
        }

        CivilianDataDTO dto = CivilianDataMapper.toDTO(saved);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/v1/api/civilians/" + saved.getId()));
        return new ResponseEntity<>(dto, headers, HttpStatus.CREATED);
    }

    private boolean notBlank(String s) { return s != null && !s.isBlank(); }

    private String clientIp(HttpServletRequest request) {
        String h = request.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) {
            int comma = h.indexOf(',');
            return comma > 0 ? h.substring(0, comma).trim() : h.trim();
        }
        return request.getRemoteAddr();
    }

    public static final class CreateRequest {
        // Auth for this request (body or headers)
        @NotBlank public String username;
        @NotBlank public String password;

        // Civilian core
        @NotBlank @Size(max = 100) public String firstName;
        @NotBlank @Size(max = 100) public String lastName;

        @Size(max = 10) public String gender;

        @Past(message = "Date of birth must be in the past")
        public LocalDate dateOfBirth;

        @NotBlank @Size(max = 50) public String nationalId;

        // Match +263 followed by exactly 9 digits, e.g. +263785689201
        @Pattern(regexp = "^\\+263\\d{9}$", message = "Phone must be +263 followed by 9 digits, e.g. +263785689201")
        @Size(min = 13, max = 13, message = "Phone must be exactly 13 characters like +263785689201")
        public String phone;

        @Email @Size(max = 150) public String email;
        @Size(max = 255) public String address;

        @Size(max = 100) public String education;
        @Size(max = 50)  public String employmentStatus;
        @Size(max = 150) public String employer;

        @Digits(integer = 10, fraction = 2)
        @DecimalMin(value = "0.00", message = "Income cannot be negative")
        public BigDecimal monthlyIncome;

        @Size(max = 50) public String maritalStatus;
        @Size(max = 5)  public String bloodType;
        @Size(max = 255) public String allergies;

        @Size(max = 100) public String bankName;
        @Size(max = 50)  public String accountNumber;

        @Min(0) @Max(1000) public Integer creditScore;

        // NEW: Added fields
        @Min(0) public Integer creditCardsCount;
        @Min(0) public Integer currentLoansCount;
    }

    public record ApiError(String message) {
        public static ApiError of(String msg) { return new ApiError(msg); }
    }
}
