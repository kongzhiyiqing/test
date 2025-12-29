package com.healthcare.repository;

import com.healthcare.model.Appointment;
import com.healthcare.util.CsvUtil;
import com.healthcare.util.HealthcareException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 预约数据仓库
 * 处理预约数据的CSV文件存储
 *
 * @author Healthcare System
 * @version 1.0
 */
public class AppointmentRepository extends CsvDataRepository<Appointment> {

    private static final String CSV_HEADER = "appointment_id,patient_id,clinician_id,facility_id,appointment_date,appointment_time,duration_minutes,appointment_type,status,reason_for_visit,notes,created_date,last_modified";

    /**
     * 构造函数
     */
    public AppointmentRepository() {
        super("appointments.csv");
    }

    /**
     * 构造函数（指定文件路径）
     */
    public AppointmentRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected String getCsvHeader() {
        return CSV_HEADER;
    }

    @Override
    protected String entityToCsvRow(Appointment appointment) {
        return CsvUtil.formatCsvRow(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getClinicianId(),
                appointment.getFacilityId(),
                CsvUtil.formatDate(appointment.getAppointmentDate()),
                CsvUtil.formatTime(appointment.getAppointmentTime()),
                CsvUtil.formatInteger(appointment.getDurationMinutes()),
                appointment.getAppointmentType(),
                appointment.getStatus(),
                appointment.getReasonForVisit(),
                appointment.getNotes(),
                CsvUtil.formatDateTime(appointment.getCreatedDate()),
                CsvUtil.formatDateTime(appointment.getLastModified())
        );
    }

    @Override
    protected Appointment csvRowToEntity(String csvRow) throws HealthcareException {
        String[] fields = CsvUtil.parseCsvRow(csvRow);

        if (fields.length < 13) {
            throw new HealthcareException("CSV行格式错误: 字段数量不足，需要13个字段，实际" + fields.length + "个");
        }

        try {
            String id = CsvUtil.emptyToNull(fields[0]);
            String patientId = CsvUtil.emptyToNull(fields[1]);
            String clinicianId = CsvUtil.emptyToNull(fields[2]);
            String facilityId = CsvUtil.emptyToNull(fields[3]);
            LocalDate appointmentDate = CsvUtil.parseDate(fields[4]);
            LocalTime appointmentTime = CsvUtil.parseTime(fields[5]);
            Integer durationMinutes = CsvUtil.parseInteger(fields[6]);
            String appointmentType = CsvUtil.emptyToNull(fields[7]);
            String status = CsvUtil.emptyToNull(fields[8]);
            String reasonForVisit = CsvUtil.emptyToNull(fields[9]);
            String notes = CsvUtil.emptyToNull(fields[10]);
            LocalDateTime createdDate = CsvUtil.parseDateTime(fields[11]);
            LocalDateTime lastModified = CsvUtil.parseDateTime(fields[12]);

            return new Appointment(id, patientId, clinicianId, facilityId, appointmentDate,
                                 appointmentTime, durationMinutes != null ? durationMinutes : 0,
                                 appointmentType, status, reasonForVisit, notes, createdDate, lastModified);

        } catch (Exception e) {
            throw new HealthcareException("解析预约数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据患者ID查找预约
     */
    public java.util.List<Appointment> findByPatientId(String patientId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(appointment -> patientId != null && patientId.equals(appointment.getPatientId()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据医生ID查找预约
     */
    public java.util.List<Appointment> findByClinicianId(String clinicianId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(appointment -> clinicianId != null && clinicianId.equals(appointment.getClinicianId()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据设施ID查找预约
     */
    public java.util.List<Appointment> findByFacilityId(String facilityId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(appointment -> facilityId != null && facilityId.equals(appointment.getFacilityId()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据日期查找预约
     */
    public java.util.List<Appointment> findByDate(LocalDate date) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(appointment -> date != null && date.equals(appointment.getAppointmentDate()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取今天的预约
     */
    public java.util.List<Appointment> findTodayAppointments() throws HealthcareException {
        return findByDate(LocalDate.now());
    }

    /**
     * 获取即将到来的预约
     */
    public java.util.List<Appointment> findUpcomingAppointments() throws HealthcareException {
        ensureLoaded();

        LocalDateTime now = LocalDateTime.now();
        return cache.stream()
                .filter(Appointment::isUpcoming)
                .sorted((a1, a2) -> a1.getAppointmentDateTime().compareTo(a2.getAppointmentDateTime()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据状态查找预约
     */
    public java.util.List<Appointment> findByStatus(String status) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(appointment -> status != null && status.equals(appointment.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 检查时间冲突
     */
    public boolean hasTimeConflict(Appointment newAppointment) throws HealthcareException {
        return hasTimeConflict(newAppointment, null);
    }

    /**
     * 检查时间冲突（排除指定预约ID）
     */
    public boolean hasTimeConflict(Appointment newAppointment, String excludeAppointmentId) throws HealthcareException {
        ensureLoaded();

        LocalDate date = newAppointment.getAppointmentDate();
        LocalTime startTime = newAppointment.getAppointmentTime();
        LocalTime endTime = newAppointment.getAppointmentEndTime();

        return cache.stream()
                .filter(apt -> !apt.getId().equals(excludeAppointmentId))
                .filter(apt -> date.equals(apt.getAppointmentDate()))
                .filter(apt -> newAppointment.getClinicianId().equals(apt.getClinicianId()) ||
                              newAppointment.getFacilityId().equals(apt.getFacilityId()))
                .anyMatch(apt -> {
                    LocalTime existingStart = apt.getAppointmentTime();
                    LocalTime existingEnd = apt.getAppointmentEndTime();
                    return !(endTime.isBefore(existingStart) || startTime.isAfter(existingEnd));
                });
    }

    /**
     * 获取预约统计信息
     */
    public String getAppointmentStatistics() throws HealthcareException {
        ensureLoaded();

        long scheduled = cache.stream().filter(Appointment::isScheduled).count();
        long completed = cache.stream().filter(Appointment::isCompleted).count();
        long cancelled = cache.stream().filter(Appointment::isCancelled).count();
        long today = findTodayAppointments().size();
        long upcoming = findUpcomingAppointments().size();

        return String.format("预约统计:\n总预约数: %d\n已预约: %d\n已完成: %d\n已取消: %d\n今日预约: %d\n即将到来: %d",
                cache.size(), scheduled, completed, cancelled, today, upcoming);
    }
}
