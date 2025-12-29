package com.healthcare.repository;

import com.healthcare.model.Referral;
import com.healthcare.util.CsvUtil;
import com.healthcare.util.HealthcareException;

import java.time.LocalDate;

/**
 * 转诊数据仓库
 * 处理转诊数据的CSV文件存储
 *
 * @author Healthcare System
 * @version 1.0
 */
public class ReferralRepository extends CsvDataRepository<Referral> {

    private static final String CSV_HEADER = "referral_id,patient_id,referring_clinician_id,referred_to_clinician_id,referring_facility_id,referred_to_facility_id,referral_date,urgency_level,referral_reason,clinical_summary,requested_investigations,status,appointment_id,notes,created_date,last_updated";

    /**
     * 构造函数
     */
    public ReferralRepository() {
        super("referrals.csv");
    }

    /**
     * 构造函数（指定文件路径）
     */
    public ReferralRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected String getCsvHeader() {
        return CSV_HEADER;
    }

    @Override
    protected String entityToCsvRow(Referral referral) {
        return CsvUtil.formatCsvRow(
                referral.getId(),
                referral.getPatientId(),
                referral.getReferringClinicianId(),
                referral.getReferredToClinicianId(),
                referral.getReferringFacilityId(),
                referral.getReferredToFacilityId(),
                CsvUtil.formatDate(referral.getReferralDate()),
                referral.getUrgencyLevel(),
                referral.getReferralReason(),
                referral.getClinicalSummary(),
                referral.getRequestedInvestigations(),
                referral.getStatus(),
                referral.getAppointmentId(),
                referral.getNotes(),
                CsvUtil.formatDate(referral.getCreatedDate()),
                CsvUtil.formatDate(referral.getLastUpdated())
        );
    }

    @Override
    protected Referral csvRowToEntity(String csvRow) throws HealthcareException {
        String[] fields = CsvUtil.parseCsvRow(csvRow);

        if (fields.length < 16) {
            throw new HealthcareException("CSV行格式错误: 字段数量不足，需要16个字段，实际" + fields.length + "个");
        }

        try {
            String id = CsvUtil.emptyToNull(fields[0]);
            String patientId = CsvUtil.emptyToNull(fields[1]);
            String referringClinicianId = CsvUtil.emptyToNull(fields[2]);
            String referredToClinicianId = CsvUtil.emptyToNull(fields[3]);
            String referringFacilityId = CsvUtil.emptyToNull(fields[4]);
            String referredToFacilityId = CsvUtil.emptyToNull(fields[5]);
            LocalDate referralDate = CsvUtil.parseDate(fields[6]);
            String urgencyLevel = CsvUtil.emptyToNull(fields[7]);
            String referralReason = CsvUtil.emptyToNull(fields[8]);
            String clinicalSummary = CsvUtil.emptyToNull(fields[9]);
            String requestedInvestigations = CsvUtil.emptyToNull(fields[10]);
            String status = CsvUtil.emptyToNull(fields[11]);
            String appointmentId = CsvUtil.emptyToNull(fields[12]);
            String notes = CsvUtil.emptyToNull(fields[13]);
            LocalDate createdDate = CsvUtil.parseDate(fields[14]);
            LocalDate lastUpdated = CsvUtil.parseDate(fields[15]);

            return new Referral(id, patientId, referringClinicianId, referredToClinicianId,
                              referringFacilityId, referredToFacilityId, referralDate, urgencyLevel,
                              referralReason, clinicalSummary, requestedInvestigations, status,
                              appointmentId, notes, createdDate, lastUpdated);

        } catch (Exception e) {
            throw new HealthcareException("解析转诊数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据患者ID查找转诊
     */
    public java.util.List<Referral> findByPatientId(String patientId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(ref -> patientId != null && patientId.equals(ref.getPatientId()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据转诊医生ID查找转诊
     */
    public java.util.List<Referral> findByReferringClinicianId(String clinicianId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(ref -> clinicianId != null && clinicianId.equals(ref.getReferringClinicianId()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据接收医生ID查找转诊
     */
    public java.util.List<Referral> findByReferredClinicianId(String clinicianId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(ref -> clinicianId != null && clinicianId.equals(ref.getReferredToClinicianId()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据转诊设施ID查找转诊
     */
    public java.util.List<Referral> findByReferringFacilityId(String facilityId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(ref -> facilityId != null && facilityId.equals(ref.getReferringFacilityId()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据接收设施ID查找转诊
     */
    public java.util.List<Referral> findByReferredFacilityId(String facilityId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(ref -> facilityId != null && facilityId.equals(ref.getReferredToFacilityId()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据状态查找转诊
     */
    public java.util.List<Referral> findByStatus(String status) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(ref -> status != null && status.equals(ref.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取紧急转诊
     */
    public java.util.List<Referral> findUrgentReferrals() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(Referral::isUrgent)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取常规转诊
     */
    public java.util.List<Referral> findRoutineReferrals() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(Referral::isRoutine)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取已完成的转诊
     */
    public java.util.List<Referral> findCompletedReferrals() throws HealthcareException {
        return findByStatus("Completed");
    }

    /**
     * 获取进行中的转诊
     */
    public java.util.List<Referral> findInProgressReferrals() throws HealthcareException {
        return findByStatus("In Progress");
    }

    /**
     * 获取新的转诊
     */
    public java.util.List<Referral> findNewReferrals() throws HealthcareException {
        return findByStatus("New");
    }

    /**
     * 获取过期的转诊
     */
    public java.util.List<Referral> findOverdueReferrals() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(Referral::isOverdue)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据预约ID查找转诊
     */
    public Referral findByAppointmentId(String appointmentId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(ref -> appointmentId != null && appointmentId.equals(ref.getAppointmentId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取转诊统计信息
     */
    public String getReferralStatistics() throws HealthcareException {
        ensureLoaded();

        long urgent = cache.stream().filter(Referral::isUrgent).count();
        long routine = cache.stream().filter(Referral::isRoutine).count();
        long completed = cache.stream().filter(Referral::isCompleted).count();
        long inProgress = cache.stream().filter(Referral::isInProgress).count();
        long newReferrals = cache.stream().filter(Referral::isNew).count();
        long overdue = cache.stream().filter(Referral::isOverdue).count();

        return String.format("转诊统计:\n总转诊数: %d\n紧急转诊: %d\n常规转诊: %d\n已完成: %d\n进行中: %d\n新转诊: %d\n已过期: %d",
                cache.size(), urgent, routine, completed, inProgress, newReferrals, overdue);
    }
}
