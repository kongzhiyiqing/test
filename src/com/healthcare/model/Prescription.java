package com.healthcare.model;

import java.time.LocalDate;

/**
 * 处方实体类
 * 表示医疗保健系统中的处方信息
 *
 * @author Healthcare System
 * @version 1.0
 */
public class Prescription extends Entity {

    private String patientId;
    private String clinicianId;
    private String appointmentId;
    private LocalDate prescriptionDate;
    private String medicationName;
    private String dosage;
    private String frequency;
    private int durationDays;
    private int quantity;
    private String instructions;
    private String pharmacyName;
    private String status;
    private LocalDate issueDate;
    private LocalDate collectionDate;

    /**
     * 默认构造函数
     */
    public Prescription() {
        super();
    }

    /**
     * 构造函数
     */
    public Prescription(String id, String patientId, String clinicianId, String appointmentId,
                        LocalDate prescriptionDate, String medicationName, String dosage,
                        String frequency, int durationDays, int quantity, String instructions,
                        String pharmacyName, String status, LocalDate issueDate, LocalDate collectionDate) {
        super(id);
        this.patientId = patientId;
        this.clinicianId = clinicianId;
        this.appointmentId = appointmentId;
        this.prescriptionDate = prescriptionDate;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.durationDays = durationDays;
        this.quantity = quantity;
        this.instructions = instructions;
        this.pharmacyName = pharmacyName;
        this.status = status;
        this.issueDate = issueDate;
        this.collectionDate = collectionDate;
    }

    // Getters and Setters
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getClinicianId() { return clinicianId; }
    public void setClinicianId(String clinicianId) { this.clinicianId = clinicianId; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public LocalDate getPrescriptionDate() { return prescriptionDate; }
    public void setPrescriptionDate(LocalDate prescriptionDate) { this.prescriptionDate = prescriptionDate; }

    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getPharmacyName() { return pharmacyName; }
    public void setPharmacyName(String pharmacyName) { this.pharmacyName = pharmacyName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }

    /**
     * 检查处方是否已发放
     */
    public boolean isIssued() {
        return "Issued".equals(status);
    }

    /**
     * 检查处方是否已收集
     */
    public boolean isCollected() {
        return "Collected".equals(status);
    }

    /**
     * 检查处方是否过期
     */
    public boolean isExpired() {
        if (issueDate == null || durationDays <= 0) return false;
        LocalDate expiryDate = issueDate.plusDays(durationDays);
        return LocalDate.now().isAfter(expiryDate);
    }

    /**
     * 获取到期日期
     */
    public LocalDate getExpiryDate() {
        if (issueDate == null) return null;
        return issueDate.plusDays(durationDays);
    }

    /**
     * 获取处方详细信息
     */
    public String getPrescriptionDetails() {
        return medicationName + " " + dosage + " - " + frequency +
               " for " + durationDays + " days (" + quantity + " units)";
    }

    @Override
    public boolean isValid() {
        return id != null && !id.trim().isEmpty() &&
               patientId != null && !patientId.trim().isEmpty() &&
               clinicianId != null && !clinicianId.trim().isEmpty() &&
               prescriptionDate != null &&
               medicationName != null && !medicationName.trim().isEmpty() &&
               dosage != null && !dosage.trim().isEmpty() &&
               frequency != null && !frequency.trim().isEmpty() &&
               durationDays > 0 &&
               quantity > 0 &&
               pharmacyName != null && !pharmacyName.trim().isEmpty() &&
               status != null && !status.trim().isEmpty();
    }

    @Override
    public String getEntityType() {
        return "Prescription";
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", medication='" + medicationName + " " + dosage + '\'' +
                ", status='" + status + '\'' +
                ", pharmacy='" + pharmacyName + '\'' +
                '}';
    }
}
