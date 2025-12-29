package com.healthcare.repository;

import com.healthcare.model.Prescription;
import com.healthcare.util.CsvUtil;
import com.healthcare.util.HealthcareException;

import java.time.LocalDate;

/**
 * 处方数据仓库
 * 处理处方数据的CSV文件存储
 *
 * @author Healthcare System
 * @version 1.0
 */
public class PrescriptionRepository extends CsvDataRepository<Prescription> {

    private static final String CSV_HEADER = "prescription_id,patient_id,clinician_id,appointment_id,prescription_date,medication_name,dosage,frequency,duration_days,quantity,instructions,pharmacy_name,status,issue_date,collection_date";

    /**
     * 构造函数
     */
    public PrescriptionRepository() {
        super("prescriptions.csv");
    }

    /**
     * 构造函数（指定文件路径）
     */
    public PrescriptionRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected String getCsvHeader() {
        return CSV_HEADER;
    }

    @Override
    protected String entityToCsvRow(Prescription prescription) {
        return CsvUtil.formatCsvRow(
                prescription.getId(),
                prescription.getPatientId(),
                prescription.getClinicianId(),
                prescription.getAppointmentId(),
                CsvUtil.formatDate(prescription.getPrescriptionDate()),
                prescription.getMedicationName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                CsvUtil.formatInteger(prescription.getDurationDays()),
                CsvUtil.formatInteger(prescription.getQuantity()),
                prescription.getInstructions(),
                prescription.getPharmacyName(),
                prescription.getStatus(),
                CsvUtil.formatDate(prescription.getIssueDate()),
                CsvUtil.formatDate(prescription.getCollectionDate())
        );
    }

    @Override
    protected Prescription csvRowToEntity(String csvRow) throws HealthcareException {
        String[] fields = CsvUtil.parseCsvRow(csvRow);

        if (fields.length < 15) {
            throw new HealthcareException("CSV行格式错误: 字段数量不足，需要15个字段，实际" + fields.length + "个");
        }

        try {
            String id = CsvUtil.emptyToNull(fields[0]);
            String patientId = CsvUtil.emptyToNull(fields[1]);
            String clinicianId = CsvUtil.emptyToNull(fields[2]);
            String appointmentId = CsvUtil.emptyToNull(fields[3]);
            LocalDate prescriptionDate = CsvUtil.parseDate(fields[4]);
            String medicationName = CsvUtil.emptyToNull(fields[5]);
            String dosage = CsvUtil.emptyToNull(fields[6]);
            String frequency = CsvUtil.emptyToNull(fields[7]);
            Integer durationDays = CsvUtil.parseInteger(fields[8]);
            Integer quantity = CsvUtil.parseInteger(fields[9]);
            String instructions = CsvUtil.emptyToNull(fields[10]);
            String pharmacyName = CsvUtil.emptyToNull(fields[11]);
            String status = CsvUtil.emptyToNull(fields[12]);
            LocalDate issueDate = CsvUtil.parseDate(fields[13]);
            LocalDate collectionDate = CsvUtil.parseDate(fields[14]);

            return new Prescription(id, patientId, clinicianId, appointmentId, prescriptionDate,
                                  medicationName, dosage, frequency,
                                  durationDays != null ? durationDays : 0,
                                  quantity != null ? quantity : 0,
                                  instructions, pharmacyName, status, issueDate, collectionDate);

        } catch (Exception e) {
            throw new HealthcareException("解析处方数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据患者ID查找处方
     */
    public java.util.List<Prescription> findByPatientId(String patientId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(rx -> patientId != null && patientId.equals(rx.getPatientId()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据医生ID查找处方
     */
    public java.util.List<Prescription> findByClinicianId(String clinicianId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(rx -> clinicianId != null && clinicianId.equals(rx.getClinicianId()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据预约ID查找处方
     */
    public java.util.List<Prescription> findByAppointmentId(String appointmentId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(rx -> appointmentId != null && appointmentId.equals(rx.getAppointmentId()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据状态查找处方
     */
    public java.util.List<Prescription> findByStatus(String status) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(rx -> status != null && status.equals(rx.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取已发放的处方
     */
    public java.util.List<Prescription> findIssuedPrescriptions() throws HealthcareException {
        return findByStatus("Issued");
    }

    /**
     * 获取已收集的处方
     */
    public java.util.List<Prescription> findCollectedPrescriptions() throws HealthcareException {
        return findByStatus("Collected");
    }

    /**
     * 获取过期的处方
     */
    public java.util.List<Prescription> findExpiredPrescriptions() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(Prescription::isExpired)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 搜索处方（按药物名称）
     */
    public java.util.List<Prescription> searchByMedication(String medicationName) throws HealthcareException {
        ensureLoaded();

        if (medicationName == null || medicationName.trim().isEmpty()) {
            return new java.util.ArrayList<>(cache);
        }

        String searchTerm = medicationName.toLowerCase().trim();
        return cache.stream()
                .filter(rx -> rx.getMedicationName() != null &&
                             rx.getMedicationName().toLowerCase().contains(searchTerm))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取即将到期的处方（7天内）
     */
    public java.util.List<Prescription> findExpiringPrescriptions() throws HealthcareException {
        ensureLoaded();

        LocalDate weekFromNow = LocalDate.now().plusDays(7);
        return cache.stream()
                .filter(rx -> rx.isIssued() && !rx.isCollected())
                .filter(rx -> {
                    LocalDate expiryDate = rx.getExpiryDate();
                    return expiryDate != null && !expiryDate.isAfter(weekFromNow);
                })
                .sorted((rx1, rx2) -> rx1.getExpiryDate().compareTo(rx2.getExpiryDate()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取处方统计信息
     */
    public String getPrescriptionStatistics() throws HealthcareException {
        ensureLoaded();

        long issued = cache.stream().filter(Prescription::isIssued).count();
        long collected = cache.stream().filter(Prescription::isCollected).count();
        long expired = cache.stream().filter(Prescription::isExpired).count();
        long expiringSoon = findExpiringPrescriptions().size();

        return String.format("处方统计:\n总处方数: %d\n已发放: %d\n已收集: %d\n已过期: %d\n即将过期: %d",
                cache.size(), issued, collected, expired, expiringSoon);
    }
}
