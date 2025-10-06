package com.cbs.system.service;

import com.cbs.system.entity.ApiRequestAudit;
import com.cbs.system.enums.RoleEnums;
import com.cbs.system.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardService {

    private final UsersRepository usersRepo;
    private final APIUsersRepository apiUsersRepo;
    private final CivilianDataRepository civiliansRepo;
    private final ApiRequestAuditRepository auditRepo;

    public DashboardService(UsersRepository usersRepo,
                            APIUsersRepository apiUsersRepo,
                            CivilianDataRepository civiliansRepo,
                            ApiRequestAuditRepository auditRepo) {
        this.usersRepo = usersRepo;
        this.apiUsersRepo = apiUsersRepo;
        this.civiliansRepo = civiliansRepo;
        this.auditRepo = auditRepo;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> m = new HashMap<>();

        // Users
        long usersTotal = usersRepo.count();
        long usersActive = usersRepo.countByActiveTrue();
        long admins = usersRepo.countByRole(RoleEnums.ADMIN);

        Map<String, Long> roleCounts = new LinkedHashMap<>();
        for (Object[] r : usersRepo.countByRoleGrouped()) {
            roleCounts.put(String.valueOf(r[0]), ((Number) r[1]).longValue());
        }

        // API Users
        long apiUsersTotal = apiUsersRepo.count();
        long apiUsersActive = apiUsersRepo.countByActiveTrue();

        // Civilians
        long civiliansTotal = civiliansRepo.count();
        BigDecimal avgIncome = civiliansRepo.averageMonthlyIncome();
        avgIncome = (avgIncome == null ? BigDecimal.ZERO : avgIncome).setScale(2, RoundingMode.HALF_UP);

        long male = civiliansRepo.countMale();
        long female = civiliansRepo.countFemale();

        // Traffic last 24h
        List<Object[]> rows = auditRepo.countsLast24h();
        Map<String, Long> hourly = new HashMap<>();
        for (Object[] r : rows) {
            hourly.put(String.valueOf(r[0]), ((Number) r[1]).longValue());
        }
        // Build 24 buckets
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 23; i >= 0; i--) {
            LocalDateTime slot = now.minusHours(i);
            String key = slot.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00"));
            labels.add(slot.format(fmt));
            values.add(hourly.getOrDefault(key, 0L));
        }

        // Latest requests
        List<ApiRequestAudit> latest = auditRepo.findTop10ByOrderByCreatedAtDesc();

        m.put("usersTotal", usersTotal);
        m.put("usersActive", usersActive);
        m.put("admins", admins);
        m.put("roleCounts", roleCounts);

        m.put("apiUsersTotal", apiUsersTotal);
        m.put("apiUsersActive", apiUsersActive);

        m.put("civiliansTotal", civiliansTotal);
        m.put("avgIncome", avgIncome);
        m.put("male", male);
        m.put("female", female);

        m.put("trafficLabels", labels);
        m.put("trafficValues", values);
        m.put("latestRequests", latest);
        return m;
    }
}
