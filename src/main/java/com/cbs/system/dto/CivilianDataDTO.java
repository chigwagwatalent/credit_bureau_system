package com.cbs.system.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CivilianDataDTO {

    private Long id;

    @NotBlank @Size(max = 100)
    private String firstName;

    @NotBlank @Size(max = 100)
    private String lastName;

    @Size(max = 10)
    private String gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank @Size(max = 50)
    private String nationalId;

    @Size(max = 20)
    private String phone;

    @Email @Size(max = 150)
    private String email;

    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String education;

    @Size(max = 50)
    private String employmentStatus;

    @Size(max = 150)
    private String employer;

    @Digits(integer = 10, fraction = 2)
    @DecimalMin(value = "0.00", message = "Income cannot be negative")
    private BigDecimal monthlyIncome;

    @Size(max = 50)
    private String maritalStatus;

    @Size(max = 5)
    private String bloodType;

    @Size(max = 255)
    private String allergies;

    @Size(max = 100)
    private String bankName;

    @Size(max = 50)
    private String accountNumber;

    @Min(0) @Max(1000)
    private Integer creditScore;

    // -------- NEW FIELDS ----------
    @Min(0)
    private Integer creditCardsCount;

    @Min(0)
    private Integer currentLoansCount;
    // ------------------------------

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    public String getEmployer() { return employer; }
    public void setEmployer(String employer) { this.employer = employer; }

    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }

    public Integer getCreditCardsCount() { return creditCardsCount; }
    public void setCreditCardsCount(Integer creditCardsCount) { this.creditCardsCount = creditCardsCount; }

    public Integer getCurrentLoansCount() { return currentLoansCount; }
    public void setCurrentLoansCount(Integer currentLoansCount) { this.currentLoansCount = currentLoansCount; }
}
