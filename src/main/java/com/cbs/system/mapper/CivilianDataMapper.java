package com.cbs.system.mapper;

import com.cbs.system.dto.CivilianDataDTO;
import com.cbs.system.entity.CivilianData;

public final class CivilianDataMapper {
    private CivilianDataMapper(){}

    public static CivilianData toEntity(CivilianDataDTO d) {
        if (d == null) return null;
        CivilianData e = new CivilianData();
        e.setId(d.getId());
        e.setFirstName(d.getFirstName());
        e.setLastName(d.getLastName());
        e.setGender(d.getGender());
        e.setDateOfBirth(d.getDateOfBirth());
        e.setNationalId(d.getNationalId());
        e.setPhone(d.getPhone());
        e.setEmail(d.getEmail());
        e.setAddress(d.getAddress());
        e.setEducation(d.getEducation());
        e.setEmploymentStatus(d.getEmploymentStatus());
        e.setEmployer(d.getEmployer());
        e.setMonthlyIncome(d.getMonthlyIncome());
        e.setMaritalStatus(d.getMaritalStatus());
        e.setBloodType(d.getBloodType());
        e.setAllergies(d.getAllergies());
        e.setBankName(d.getBankName());
        e.setAccountNumber(d.getAccountNumber());
        e.setCreditScore(d.getCreditScore());
        return e;
    }

    public static CivilianDataDTO toDTO(CivilianData e) {
        if (e == null) return null;
        CivilianDataDTO d = new CivilianDataDTO();
        d.setId(e.getId());
        d.setFirstName(e.getFirstName());
        d.setLastName(e.getLastName());
        d.setGender(e.getGender());
        d.setDateOfBirth(e.getDateOfBirth());
        d.setNationalId(e.getNationalId());
        d.setPhone(e.getPhone());
        d.setEmail(e.getEmail());
        d.setAddress(e.getAddress());
        d.setEducation(e.getEducation());
        d.setEmploymentStatus(e.getEmploymentStatus());
        d.setEmployer(e.getEmployer());
        d.setMonthlyIncome(e.getMonthlyIncome());
        d.setMaritalStatus(e.getMaritalStatus());
        d.setBloodType(e.getBloodType());
        d.setAllergies(e.getAllergies());
        d.setBankName(e.getBankName());
        d.setAccountNumber(e.getAccountNumber());
        d.setCreditScore(e.getCreditScore());
        return d;
    }

    public static void copyToEntity(CivilianDataDTO d, CivilianData e){
        if (d == null || e == null) return;
        e.setFirstName(d.getFirstName());
        e.setLastName(d.getLastName());
        e.setGender(d.getGender());
        e.setDateOfBirth(d.getDateOfBirth());
        e.setNationalId(d.getNationalId());
        e.setPhone(d.getPhone());
        e.setEmail(d.getEmail());
        e.setAddress(d.getAddress());
        e.setEducation(d.getEducation());
        e.setEmploymentStatus(d.getEmploymentStatus());
        e.setEmployer(d.getEmployer());
        e.setMonthlyIncome(d.getMonthlyIncome());
        e.setMaritalStatus(d.getMaritalStatus());
        e.setBloodType(d.getBloodType());
        e.setAllergies(d.getAllergies());
        e.setBankName(d.getBankName());
        e.setAccountNumber(d.getAccountNumber());
        e.setCreditScore(d.getCreditScore());
    }
}
