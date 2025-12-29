package com.healthcare.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * 预约实体类
 * 表示医疗保健系统中的预约信息
 *
 * @author Healthcare System
 * @version 1.0
 */
public class Appointment extends Entity {

    private String patientId;
    private String clinicianId;
    private String facilityId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private int durationMinutes;
    private String appointmentType;
    private String status;
    private String reasonForVisit;
    private String notes;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;

    /**
     * 默认构造函数
     */
    public Appointment() {
        super();
    }

    /**
     * 构造函数
     */
    public Appointment(String id, String patientId, String clinicianId, String facilityId,
                       LocalDate appointmentDate, LocalTime appointmentTime, int durationMinutes,
                       String appointmentType, String status, String reasonForVisit,
                       String notes, LocalDateTime createdDate, LocalDateTime lastModified) {
        super(id);
        this.patientId = patientId;
        this.clinicianId = clinicianId;
        this.facilityId = facilityId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.durationMinutes = durationMinutes;
        this.appointmentType = appointmentType;
        this.status = status;
        this.reasonForVisit = reasonForVisit;
        this.notes = notes;
        this.createdDate = createdDate;
        this.lastModified = lastModified;
    }

    // Getters and Setters
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getClinicianId() { return clinicianId; }
    public void setClinicianId(String clinicianId) { this.clinicianId = clinicianId; }

    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getAppointmentType() { return appointmentType; }
    public void setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReasonForVisit() { return reasonForVisit; }
    public void setReasonForVisit(String reasonForVisit) { this.reasonForVisit = reasonForVisit; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }

    /**
     * 获取完整的预约日期时间
     */
    public LocalDateTime getAppointmentDateTime() {
        if (appointmentDate == null || appointmentTime == null) {
            return null;
        }
        return LocalDateTime.of(appointmentDate, appointmentTime);
    }

    /**
     * 获取预约结束时间
     */
    public LocalTime getAppointmentEndTime() {
        if (appointmentTime == null) {
            return null;
        }
        return appointmentTime.plusMinutes(durationMinutes);
    }

    /**
     * 检查预约是否为今天
     */
    public boolean isToday() {
        return appointmentDate != null && appointmentDate.equals(LocalDate.now());
    }

    /**
     * 检查预约是否为未来预约
     */
    public boolean isUpcoming() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime appointmentDateTime = getAppointmentDateTime();
        return appointmentDateTime != null && appointmentDateTime.isAfter(now);
    }

    /**
     * 检查预约是否已完成
     */
    public boolean isCompleted() {
        return "Completed".equals(status);
    }

    /**
     * 检查预约是否已取消
     */
    public boolean isCancelled() {
        return "Cancelled".equals(status);
    }

    /**
     * 检查预约是否已预定
     */
    public boolean isScheduled() {
        return "Scheduled".equals(status);
    }

    @Override
    public boolean isValid() {
        return id != null && !id.trim().isEmpty() &&
               patientId != null && !patientId.trim().isEmpty() &&
               clinicianId != null && !clinicianId.trim().isEmpty() &&
               facilityId != null && !facilityId.trim().isEmpty() &&
               appointmentDate != null &&
               appointmentTime != null &&
               durationMinutes > 0 &&
               appointmentType != null && !appointmentType.trim().isEmpty() &&
               status != null && !status.trim().isEmpty();
    }

    @Override
    public String getEntityType() {
        return "Appointment";
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", clinicianId='" + clinicianId + '\'' +
                ", dateTime=" + getAppointmentDateTime() +
                ", type='" + appointmentType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
