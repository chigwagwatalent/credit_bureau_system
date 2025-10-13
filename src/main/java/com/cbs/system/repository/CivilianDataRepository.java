package com.cbs.system.repository;

import com.cbs.system.entity.CivilianData;
import org.springframework.data.domain.Page;              // correct Page
import org.springframework.data.domain.Pageable;       // ✅ correct Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface CivilianDataRepository extends JpaRepository<CivilianData, Long> {

    @Query("select coalesce(avg(c.monthlyIncome), 0) from CivilianData c")
    BigDecimal averageMonthlyIncome();

    @Query("select coalesce(sum(c.monthlyIncome), 0) from CivilianData c")
    BigDecimal totalMonthlyIncome();

    @Query("select count(c) from CivilianData c where lower(c.gender) = 'male'")
    long countMale();

    @Query("select count(c) from CivilianData c where lower(c.gender) = 'female'")
    long countFemale();

    Optional<CivilianData> findByEmail(String email);

    Optional<CivilianData> findByNationalId(String nationalId);

    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);

    Page<CivilianData> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrNationalIdContainingIgnoreCase(
            String firstName, String lastName, String nationalId, Pageable pageable
    );
}
