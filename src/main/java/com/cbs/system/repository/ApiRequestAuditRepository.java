package com.cbs.system.repository;

import com.cbs.system.entity.ApiRequestAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApiRequestAuditRepository extends JpaRepository<ApiRequestAudit, Long> {

    @Query(value = """
        SELECT DATE_FORMAT(created_at, '%Y-%m-%d %H:00:00') AS hour_key,
               COUNT(*) AS total
        FROM api_request_audit
        WHERE created_at >= (NOW() - INTERVAL 23 HOUR)
        GROUP BY hour_key
        ORDER BY hour_key
        """, nativeQuery = true)
    List<Object[]> countsLast24h();

    List<ApiRequestAudit> findTop10ByOrderByCreatedAtDesc();
}
