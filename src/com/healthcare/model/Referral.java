package com.healthcare.model;

import java.time.LocalDate;

/**
 * 转诊实体类
 * 表示医疗保健系统中的转诊信息
 *
 * @author Healthcare System
 * @version 1.0
 */
public class Referral extends Entity {

    private String patientId;
    private String referringClinicianId;
    private String referredToClinicianId;
    private String referringFacilityId;
    private String referredToFacilityId;
    private LocalDate referralDate;
    private String urgencyLevel;
    private String referralReason;
    private String clinicalSummary;
    private String requestedInvestigations;
    private String status;
    private String appointmentId;
    private String notes;
    private LocalDate createdDate;
    private LocalDate lastUpdated;

    /**
     * 默认构造函数
     */
    public Referral() {
        super();
    }

    /**
     * 构造函数
     */
    public Referral(String id, String patientId, String referringClinicianId,
                    String referredToClinicianId, String referringFacilityId,
                    String referredToFacilityId, LocalDate referralDate, String urgencyLevel,
                    String referralReason, String clinicalSummary, String requestedInvestigations,
                    String status, String appointmentId, String notes, LocalDate createdDate,
                    LocalDate lastUpdated) {
        super(id);
        this.patientId = patientId;
        this.referringClinicianId = referringClinicianId;
        this.referredToClinicianId = referredToClinicianId;
        this.referringFacilityId = referringFacilityId;
        this.referredToFacilityId = referredToFacilityId;
        this.referralDate = referralDate;
        this.urgencyLevel = urgencyLevel;
        this.referralReason = referralReason;
        this.clinicalSummary = clinicalSummary;
        this.requestedInvestigations = requestedInvestigations;
        this.status = status;
        this.appointmentId = appointmentId;
        this.notes = notes;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getReferringClinicianId() { return referringClinicianId; }
    public void setReferringClinicianId(String referringClinicianId) { this.referringClinicianId = referringClinicianId; }

    public String getReferredToClinicianId() { return referredToClinicianId; }
    public void setReferredToClinicianId(String referredToClinicianId) { this.referredToClinicianId = referredToClinicianId; }

    public String getReferringFacilityId() { return referringFacilityId; }
    public void setReferringFacilityId(String referringFacilityId) { this.referringFacilityId = referringFacilityId; }

    public String getReferredToFacilityId() { return referredToFacilityId; }
    public void setReferredToFacilityId(String referredToFacilityId) { this.referredToFacilityId = referredToFacilityId; }

    public LocalDate getReferralDate() { return referralDate; }
    public void setReferralDate(LocalDate referralDate) { this.referralDate = referralDate; }

    public String getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public String getReferralReason() { return referralReason; }
    public void setReferralReason(String referralReason) { this.referralReason = referralReason; }

    public String getClinicalSummary() { return clinicalSummary; }
    public void setClinicalSummary(String clinicalSummary) { this.clinicalSummary = clinicalSummary; }

    public String getRequestedInvestigations() { return requestedInvestigations; }
    public void setRequestedInvestigations(String requestedInvestigations) { this.requestedInvestigations = requestedInvestigations; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }

    /**
     * 检查转诊是否为紧急
     */
    public boolean isUrgent() {
        return "Urgent".equals(urgencyLevel);
    }

    /**
     * 检查转诊是否为常规
     */
    public boolean isRoutine() {
        return "Routine".equals(urgencyLevel);
    }

    /**
     * 检查转诊是否已完成
     */
    public boolean isCompleted() {
        return "Completed".equals(status);
    }

    /**
     * 检查转诊是否正在进行中
     */
    public boolean isInProgress() {
        return "In Progress".equals(status);
    }

    /**
     * 检查转诊是否为新转诊
     */
    public boolean isNew() {
        return "New".equals(status);
    }

    /**
     * 检查转诊是否为待处理
     */
    public boolean isPending() {
        return "Pending".equals(status);
    }

    /**
     * 获取转诊天数
     */
    public int getDaysSinceReferral() {
        if (referralDate == null) return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(referralDate, LocalDate.now());
    }

    /**
     * 检查转诊是否过期（超过30天）
     */
    public boolean isOverdue() {
        return getDaysSinceReferral() > 30 && !isCompleted();
    }

    @Override
    public boolean isValid() {
        return id != null && !id.trim().isEmpty() &&
               patientId != null && !patientId.trim().isEmpty() &&
               referringClinicianId != null && !referringClinicianId.trim().isEmpty() &&
               referredToClinicianId != null && !referredToClinicianId.trim().isEmpty() &&
               referringFacilityId != null && !referringFacilityId.trim().isEmpty() &&
               referredToFacilityId != null && !referredToFacilityId.trim().isEmpty() &&
               referralDate != null &&
               urgencyLevel != null && !urgencyLevel.trim().isEmpty() &&
               referralReason != null && !referralReason.trim().isEmpty() &&
               status != null && !status.trim().isEmpty();
    }

    @Override
    public String getEntityType() {
        return "Referral";
    }

    @Override
    public String toString() {
        return "Referral{" +
                "id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", from='" + referringClinicianId + " (" + referringFacilityId + ")" +
                ", to='" + referredToClinicianId + " (" + referredToFacilityId + ")" +
                ", urgency='" + urgencyLevel + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
