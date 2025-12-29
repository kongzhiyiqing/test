package com.healthcare.model;

import java.time.LocalDate;

/**
 * 临床医生实体类
 * 表示医疗保健系统中的医生和临床工作人员
 *
 * @author Healthcare System
 * @version 1.0
 */
public class Clinician extends Entity {

    private String firstName;
    private String lastName;
    private String title;
    private String speciality;
    private String gmcNumber;
    private String phoneNumber;
    private String email;
    private String workplaceId;
    private String workplaceType;
    private String employmentStatus;
    private LocalDate startDate;

    /**
     * 默认构造函数
     */
    public Clinician() {
        super();
    }

    /**
     * 构造函数
     */
    public Clinician(String id, String firstName, String lastName, String title,
                     String speciality, String gmcNumber, String phoneNumber, String email,
                     String workplaceId, String workplaceType, String employmentStatus,
                     LocalDate startDate) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.speciality = speciality;
        this.gmcNumber = gmcNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.workplaceId = workplaceId;
        this.workplaceType = workplaceType;
        this.employmentStatus = employmentStatus;
        this.startDate = startDate;
    }

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSpeciality() { return speciality; }
    public void setSpeciality(String speciality) { this.speciality = speciality; }

    public String getGmcNumber() { return gmcNumber; }
    public void setGmcNumber(String gmcNumber) { this.gmcNumber = gmcNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWorkplaceId() { return workplaceId; }
    public void setWorkplaceId(String workplaceId) { this.workplaceId = workplaceId; }

    public String getWorkplaceType() { return workplaceType; }
    public void setWorkplaceType(String workplaceType) { this.workplaceType = workplaceType; }

    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    /**
     * 获取医生全名
     */
    public String getFullName() {
        return (title != null ? title + " " : "") +
               (firstName != null ? firstName : "") + " " +
               (lastName != null ? lastName : "");
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
     * 检查是否为医院工作人员
     */
    public boolean isHospitalStaff() {
        return "Hospital".equals(workplaceType);
    }

    /**
     * 检查是否为GP诊所工作人员
     */
    public boolean isGPSurgeryStaff() {
        return "GP Surgery".equals(workplaceType);
    }

    @Override
    public boolean isValid() {
        return id != null && !id.trim().isEmpty() &&
               firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               title != null && !title.trim().isEmpty() &&
               speciality != null && !speciality.trim().isEmpty() &&
               workplaceId != null && !workplaceId.trim().isEmpty() &&
               workplaceType != null && !workplaceType.trim().isEmpty();
    }

    @Override
    public String getEntityType() {
        return "Clinician";
    }

    @Override
    public String toString() {
        return "Clinician{" +
                "id='" + id + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", speciality='" + speciality + '\'' +
                ", workplace='" + workplaceId + " (" + workplaceType + ")" +
                ", status='" + employmentStatus + '\'' +
                '}';
    }
}
