package com.cbs.system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "civilian_data")
public class CivilianData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "national_id", unique = true, length = 50)
    private String nationalId;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 150, unique = true)
    private String email;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "education", length = 100)
    private String education;

    @Column(name = "employment_status", length = 50)
    private String employmentStatus;

    @Column(name = "employer", length = 150)
    private String employer;

    @Column(name = "monthly_income", precision = 10, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "marital_status", length = 50)
    private String maritalStatus;

    @Column(name = "blood_type", length = 5)
    private String bloodType;

    @Column(name = "allergies", length = 255)
    private String allergies;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "credit_score")
    private Integer creditScore;

    @Min(0)
    @Column(name = "credit_cards_count", nullable = false)
    private Integer creditCardsCount = 0;

    @Min(0)
    @Column(name = "current_loans_count", nullable = false)
    private Integer currentLoansCount = 0;
    
    public CivilianData() {}

    public CivilianData(String firstName, String lastName, String gender, LocalDate dateOfBirth,
                        String nationalId, String phone, String email, String address,
                        String education, String employmentStatus, String employer,
                        BigDecimal monthlyIncome, String maritalStatus, String bloodType,
                        String allergies, String bankName, String accountNumber,
                        Integer creditScore) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.nationalId = nationalId;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.education = education;
        this.employmentStatus = employmentStatus;
        this.employer = employer;
        this.monthlyIncome = monthlyIncome;
        this.maritalStatus = maritalStatus;
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.creditScore = creditScore;
    }

    public CivilianData(String firstName, String lastName, String gender, LocalDate dateOfBirth,
                        String nationalId, String phone, String email, String address,
                        String education, String employmentStatus, String employer,
                        BigDecimal monthlyIncome, String maritalStatus, String bloodType,
                        String allergies, String bankName, String accountNumber,
                        Integer creditScore, Integer creditCardsCount, Integer currentLoansCount) {
        this(firstName, lastName, gender, dateOfBirth, nationalId, phone, email, address,
             education, employmentStatus, employer, monthlyIncome, maritalStatus, bloodType,
             allergies, bankName, accountNumber, creditScore);
        this.creditCardsCount = creditCardsCount;
        this.currentLoansCount = currentLoansCount;
    }

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
    public void setCreditCardsCount(Integer creditCardsCount) {
        this.creditCardsCount = (creditCardsCount == null || creditCardsCount < 0) ? 0 : creditCardsCount;
    }

    public Integer getCurrentLoansCount() { return currentLoansCount; }
    public void setCurrentLoansCount(Integer currentLoansCount) {
        this.currentLoansCount = (currentLoansCount == null || currentLoansCount < 0) ? 0 : currentLoansCount;
    }

    @PrePersist
    @PreUpdate
    private void ensureNonNullCounts() {
        if (creditCardsCount == null || creditCardsCount < 0) creditCardsCount = 0;
        if (currentLoansCount == null || currentLoansCount < 0) currentLoansCount = 0;
    }

    @Override
    public String toString() {
        return "CivilianData{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", nationalId='" + nationalId + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", education='" + education + '\'' +
                ", employmentStatus='" + employmentStatus + '\'' +
                ", employer='" + employer + '\'' +
                ", monthlyIncome=" + monthlyIncome +
                ", maritalStatus='" + maritalStatus + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", allergies='" + allergies + '\'' +
                ", bankName='" + bankName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", creditScore=" + creditScore +
                ", creditCardsCount=" + creditCardsCount +
                ", currentLoansCount=" + currentLoansCount +
                '}';
    }
}
