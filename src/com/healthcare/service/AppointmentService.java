package com.healthcare.service;

import com.healthcare.model.Appointment;
import com.healthcare.model.Clinician;
import com.healthcare.model.Facility;
import com.healthcare.model.Patient;
import com.healthcare.util.HealthcareException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 预约服务
 * 处理预约相关的复杂业务逻辑
 *
 * @author Healthcare System
 * @version 1.0
 */
public class AppointmentService extends BaseService {

    @Override
    public void initialize() throws HealthcareException {
        logServiceEvent("正在初始化预约服务...");
        // 验证必要的Repository
        validateParameter(repositoryManager.getAppointmentRepository(), "AppointmentRepository");
        validateParameter(repositoryManager.getPatientRepository(), "PatientRepository");
        validateParameter(repositoryManager.getClinicianRepository(), "ClinicianRepository");
        validateParameter(repositoryManager.getFacilityRepository(), "FacilityRepository");
        initialized = true;
        logServiceEvent("预约服务初始化完成");
    }

    /**
     * 创建预约
     */
    public Appointment createAppointment(Appointment appointment) throws HealthcareException {
        validateInitialized();
        validateParameter(appointment, "appointment");

        return executeOperation("createAppointment", () -> {
            try {
                // 验证预约数据
                validateAppointmentData(appointment);

                // 检查时间冲突
                if (repositoryManager.getAppointmentRepository().hasTimeConflict(appointment)) {
                    throw new HealthcareException("预约时间存在冲突");
                }

                // 设置创建时间
                appointment.setCreatedDate(LocalDateTime.now());
                appointment.setLastModified(LocalDateTime.now());
                appointment.setStatus("Scheduled");

                // 保存预约
                repositoryManager.getAppointmentRepository().save(appointment);
                repositoryManager.flushAllData();

                logServiceEvent("预约创建成功: " + appointment.getId());
                return appointment;

            } catch (HealthcareException e) {
                logServiceEvent("预约创建失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 预约预约
     */
    public Appointment bookAppointment(String patientId, String clinicianId, String facilityId,
                                     LocalDate date, LocalTime time, int durationMinutes,
                                     String appointmentType, String reason) throws HealthcareException {
        validateInitialized();
        validateStringParameter(patientId, "patientId");
        validateStringParameter(clinicianId, "clinicianId");
        validateStringParameter(facilityId, "facilityId");
        validateParameter(date, "date");
        validateParameter(time, "time");

        return executeOperation("bookAppointment", () -> {
            try {
                // 生成预约ID
                String appointmentId = generateAppointmentId();

                // 创建预约对象
                Appointment appointment = new Appointment(appointmentId, patientId, clinicianId,
                                                        facilityId, date, time, durationMinutes,
                                                        appointmentType, "Scheduled", reason, null,
                                                        LocalDateTime.now(), LocalDateTime.now());

                return createAppointment(appointment);

            } catch (HealthcareException e) {
                logServiceEvent("预约预约失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 取消预约
     */
    public void cancelAppointment(String appointmentId, String reason) throws HealthcareException {
        validateInitialized();
        validateStringParameter(appointmentId, "appointmentId");

        recordOperation("cancelAppointment", () -> {
            try {
                // 检查预约是否存在
                Optional<Appointment> appointmentOpt = repositoryManager.getAppointmentRepository().findById(appointmentId);
                if (!appointmentOpt.isPresent()) {
                    throw new HealthcareException("预约不存在: " + appointmentId);
                }

                Appointment appointment = appointmentOpt.get();

                // 检查是否可以取消
                if ("Completed".equals(appointment.getStatus())) {
                    throw new HealthcareException("已完成的预约不能取消");
                }

                if ("Cancelled".equals(appointment.getStatus())) {
                    throw new HealthcareException("预约已被取消");
                }

                // 检查取消时间（至少提前24小时）
                LocalDateTime appointmentDateTime = appointment.getAppointmentDateTime();
                if (appointmentDateTime != null &&
                    appointmentDateTime.minusHours(24).isBefore(LocalDateTime.now())) {
                    throw new HealthcareException("预约时间不足24小时，无法取消");
                }

                appointment.setStatus("Cancelled");
                appointment.setNotes(reason != null ? "取消原因: " + reason : "用户取消");
                appointment.setLastModified(LocalDateTime.now());

                repositoryManager.getAppointmentRepository().save(appointment);
                repositoryManager.flushAllData();

                logServiceEvent("预约取消成功: " + appointmentId);

            } catch (HealthcareException e) {
                logServiceEvent("预约取消失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 重新安排预约
     */
    public Appointment rescheduleAppointment(String appointmentId, LocalDate newDate, LocalTime newTime)
            throws HealthcareException {
        validateInitialized();
        validateStringParameter(appointmentId, "appointmentId");
        validateParameter(newDate, "newDate");
        validateParameter(newTime, "newTime");

        return executeOperation("rescheduleAppointment", () -> {
            try {
                // 检查预约是否存在
                Optional<Appointment> appointmentOpt = repositoryManager.getAppointmentRepository().findById(appointmentId);
                if (!appointmentOpt.isPresent()) {
                    throw new HealthcareException("预约不存在: " + appointmentId);
                }

                Appointment appointment = appointmentOpt.get();

                // 检查是否可以重新安排
                if ("Completed".equals(appointment.getStatus())) {
                    throw new HealthcareException("已完成的预约不能重新安排");
                }

                if ("Cancelled".equals(appointment.getStatus())) {
                    throw new HealthcareException("已取消的预约不能重新安排");
                }

                // 创建新的预约时间
                Appointment newAppointment = new Appointment(
                    appointment.getId(),
                    appointment.getPatientId(),
                    appointment.getClinicianId(),
                    appointment.getFacilityId(),
                    newDate,
                    newTime,
                    appointment.getDurationMinutes(),
                    appointment.getAppointmentType(),
                    appointment.getStatus(),
                    appointment.getReasonForVisit(),
                    appointment.getNotes(),
                    appointment.getCreatedDate(),
                    LocalDateTime.now()
                );

                // 验证新预约数据
                validateAppointmentData(newAppointment);

                // 检查时间冲突
                if (repositoryManager.getAppointmentRepository().hasTimeConflict(newAppointment, appointmentId)) {
                    throw new HealthcareException("新预约时间存在冲突");
                }

                repositoryManager.getAppointmentRepository().save(newAppointment);
                repositoryManager.flushAllData();

                logServiceEvent("预约重新安排成功: " + appointmentId);
                return newAppointment;

            } catch (HealthcareException e) {
                logServiceEvent("预约重新安排失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 完成预约
     */
    public void completeAppointment(String appointmentId, String notes) throws HealthcareException {
        validateInitialized();
        validateStringParameter(appointmentId, "appointmentId");

        recordOperation("completeAppointment", () -> {
            try {
                // 检查预约是否存在
                Optional<Appointment> appointmentOpt = repositoryManager.getAppointmentRepository().findById(appointmentId);
                if (!appointmentOpt.isPresent()) {
                    throw new HealthcareException("预约不存在: " + appointmentId);
                }

                Appointment appointment = appointmentOpt.get();

                // 检查是否可以完成
                if ("Cancelled".equals(appointment.getStatus())) {
                    throw new HealthcareException("已取消的预约不能标记为完成");
                }

                if ("Completed".equals(appointment.getStatus())) {
                    throw new HealthcareException("预约已完成");
                }

                appointment.setStatus("Completed");
                if (notes != null && !notes.trim().isEmpty()) {
                    appointment.setNotes((appointment.getNotes() != null ? appointment.getNotes() + "; " : "") + notes);
                }
                appointment.setLastModified(LocalDateTime.now());

                repositoryManager.getAppointmentRepository().save(appointment);
                repositoryManager.flushAllData();

                logServiceEvent("预约完成: " + appointmentId);

            } catch (HealthcareException e) {
                logServiceEvent("预约完成失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 获取可用预约时段
     */
    public List<TimeSlot> getAvailableTimeSlots(String clinicianId, String facilityId, LocalDate date)
            throws HealthcareException {
        validateInitialized();
        validateStringParameter(clinicianId, "clinicianId");
        validateStringParameter(facilityId, "facilityId");
        validateParameter(date, "date");

        return executeOperation("getAvailableTimeSlots", () -> {
            try {
                // 获取工作时间（假设9:00-17:00）
                LocalTime startTime = LocalTime.of(9, 0);
                LocalTime endTime = LocalTime.of(17, 0);
                int slotDuration = 15; // 15分钟时段

                List<TimeSlot> availableSlots = new java.util.ArrayList<>();

                LocalTime currentTime = startTime;
                while (currentTime.isBefore(endTime)) {
                    TimeSlot slot = new TimeSlot(date, currentTime, slotDuration);

                    // 检查是否有冲突
                    Appointment testAppointment = new Appointment(null, null, clinicianId, facilityId,
                                                                date, currentTime, slotDuration, null, null, null, null, null, null);

                    if (!repositoryManager.getAppointmentRepository().hasTimeConflict(testAppointment)) {
                        availableSlots.add(slot);
                    }

                    currentTime = currentTime.plusMinutes(slotDuration);
                }

                return availableSlots;

            } catch (HealthcareException e) {
                logServiceEvent("获取可用时段失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 验证预约数据
     */
    private void validateAppointmentData(Appointment appointment) throws HealthcareException {
        validateBusinessRule(appointment.isValid(), "预约数据不符合要求");

        // 验证患者存在
        validateBusinessRule(repositoryManager.getPatientRepository().existsById(appointment.getPatientId()),
                           "患者不存在: " + appointment.getPatientId());

        // 验证医生存在
        validateBusinessRule(repositoryManager.getClinicianRepository().existsById(appointment.getClinicianId()),
                           "医生不存在: " + appointment.getClinicianId());

        // 验证设施存在
        validateBusinessRule(repositoryManager.getFacilityRepository().existsById(appointment.getFacilityId()),
                           "医疗设施不存在: " + appointment.getFacilityId());

        // 验证预约时间是未来时间
        LocalDateTime appointmentDateTime = appointment.getAppointmentDateTime();
        validateBusinessRule(appointmentDateTime != null && appointmentDateTime.isAfter(LocalDateTime.now()),
                           "预约时间必须是未来时间");

        // 验证预约时长合理
        validateBusinessRule(appointment.getDurationMinutes() >= 5 && appointment.getDurationMinutes() <= 480,
                           "预约时长必须在5-480分钟之间");
    }

    /**
     * 生成预约ID
     */
    private String generateAppointmentId() {
        // 简单的ID生成策略：A + 时间戳 + 随机数
        return "A" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    /**
     * 获取预约提醒列表
     */
    public List<Appointment> getAppointmentReminders() throws HealthcareException {
        validateInitialized();

        return executeOperation("getAppointmentReminders", () -> {
            try {
                // 获取未来24小时内的预约
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime tomorrow = now.plusDays(1);

                return repositoryManager.getAppointmentRepository().findAll().stream()
                    .filter(apt -> apt.getAppointmentDateTime() != null)
                    .filter(apt -> apt.getAppointmentDateTime().isAfter(now))
                    .filter(apt -> apt.getAppointmentDateTime().isBefore(tomorrow))
                    .filter(apt -> "Scheduled".equals(apt.getStatus()))
                    .sorted((a1, a2) -> a1.getAppointmentDateTime().compareTo(a2.getAppointmentDateTime()))
                    .collect(java.util.stream.Collectors.toList());

            } catch (Exception e) {
                logServiceEvent("获取预约提醒失败: " + e.getMessage());
                throw new HealthcareException("获取预约提醒失败", e);
            }
        });
    }

    @Override
    public String getServiceStatus() {
        return getBasicStatus() +
               ", 预约总数: " + (initialized ?
                   repositoryManager.getAppointmentRepository().getCacheSize() : "未知");
    }

    @Override
    public String getStatistics() {
        if (!initialized) {
            return "服务未初始化";
        }

        try {
            StringBuilder stats = new StringBuilder();
            stats.append("预约服务统计:\n");
            stats.append("- 预约总数: ").append(repositoryManager.getAppointmentRepository().count()).append("\n");
            stats.append(repositoryManager.getAppointmentRepository().getAppointmentStatistics());
            return stats.toString();
        } catch (Exception e) {
            return "获取统计信息失败: " + e.getMessage();
        }
    }

    /**
     * 时段内部类
     */
    public static class TimeSlot {
        private final LocalDate date;
        private final LocalTime time;
        private final int durationMinutes;

        public TimeSlot(LocalDate date, LocalTime time, int durationMinutes) {
            this.date = date;
            this.time = time;
            this.durationMinutes = durationMinutes;
        }

        public LocalDate getDate() { return date; }
        public LocalTime getTime() { return time; }
        public int getDurationMinutes() { return durationMinutes; }
        public LocalTime getEndTime() { return time.plusMinutes(durationMinutes); }

        @Override
        public String toString() {
            return date + " " + time + "-" + getEndTime();
        }
    }
}
