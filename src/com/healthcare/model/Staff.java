package com.healthcare.model;

import java.time.LocalDate;

/**
 * 工作人员实体类
 * 表示医疗保健系统中的行政和管理人员
 *
 * @author Healthcare System
 * @version 1.0
 */
public class Staff extends Entity {

    private String firstName;
    private String lastName;
    private String role;
    private String department;
    private String facilityId;
    private String phoneNumber;
    private String email;
    private String employmentStatus;
    private LocalDate startDate;
    private String lineManager;
    private String accessLevel;

    /**
     * 默认构造函数
     */
    public Staff() {
        super();
    }

    /**
     * 构造函数
     */
    public Staff(String id, String firstName, String lastName, String role, String department,
                 String facilityId, String phoneNumber, String email, String employmentStatus,
                 LocalDate startDate, String lineManager, String accessLevel) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.department = department;
        this.facilityId = facilityId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.employmentStatus = employmentStatus;
        this.startDate = startDate;
        this.lineManager = lineManager;
        this.accessLevel = accessLevel;
    }

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public String getLineManager() { return lineManager; }
    public void setLineManager(String lineManager) { this.lineManager = lineManager; }

    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }

    /**
     * 获取工作人员全名
     */
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    /**
     * 获取工作年限
     */
    public int getYearsOfService() {
        if (startDate == null) return 0;
        return LocalDate.now().getYear() - startDate.getYear();
    }

    /**
     * 检查是否为全职员工
     */
    public boolean isFullTime() {
        return "Full-time".equals(employmentStatus);
    }

    /**
     * 检查是否为兼职员工
     */
    public boolean isPartTime() {
        return "Part-time".equals(employmentStatus);
    }

    /**
     * 检查是否为管理人员
     */
    public boolean isManager() {
        return "Manager".equals(accessLevel);
    }

    /**
     * 检查是否为标准用户
     */
    public boolean isStandardUser() {
        return "Standard".equals(accessLevel);
    }

    /**
     * 检查是否为基本用户
     */
    public boolean isBasicUser() {
        return "Basic".equals(accessLevel);
    }

    /**
     * 检查是否为诊所经理
     */
    public boolean isPracticeManager() {
        return "Practice Manager".equals(role);
    }

    /**
     * 检查是否为接待员
     */
    public boolean isReceptionist() {
        return "Receptionist".equals(role);
    }

    /**
     * 检查是否为医疗秘书
     */
    public boolean isMedicalSecretary() {
        return "Medical Secretary".equals(role);
    }

    /**
     * 检查是否为医疗助理
     */
    public boolean isHealthcareAssistant() {
        return "Healthcare Assistant".equals(role);
    }

    @Override
    public boolean isValid() {
        return id != null && !id.trim().isEmpty() &&
               firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               role != null && !role.trim().isEmpty() &&
               department != null && !department.trim().isEmpty() &&
               facilityId != null && !facilityId.trim().isEmpty() &&
               employmentStatus != null && !employmentStatus.trim().isEmpty() &&
               accessLevel != null && !accessLevel.trim().isEmpty();
    }

    @Override
    public String getEntityType() {
        return "Staff";
    }

    @Override
    public String toString() {
        return "Staff{" +
                "id='" + id + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", role='" + role + '\'' +
                ", department='" + department + '\'' +
                ", facility='" + facilityId + '\'' +
                ", accessLevel='" + accessLevel + '\'' +
                '}';
    }
}
