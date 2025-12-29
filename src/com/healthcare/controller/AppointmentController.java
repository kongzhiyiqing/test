package com.healthcare.controller;

import com.healthcare.model.Appointment;
import com.healthcare.util.HealthcareException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 预约管理控制器
 * 处理预约相关的业务逻辑
 *
 * @author Healthcare System
 * @version 1.0
 */
public class AppointmentController extends BaseController {

    private List<Appointment> appointments;

    /**
     * 构造函数
     */
    public AppointmentController() {
        this.appointments = new ArrayList<>();
        initializeSampleData();
    }

    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        appointments.add(new Appointment("A001", "P001", "C001", "S001",
                LocalDate.of(2025, 9, 20), LocalTime.of(9, 0), 15,
                "Routine Consultation", "Scheduled", "Annual health check",
                "Patient due for routine screening", LocalDateTime.of(2025, 9, 15, 10, 0),
                LocalDateTime.of(2025, 9, 15, 10, 0)));

        appointments.add(new Appointment("A002", "P002", "C009", "S001",
                LocalDate.of(2025, 9, 20), LocalTime.of(10, 30), 30,
                "Vaccination", "Scheduled", "Flu vaccination",
                "Annual flu jab appointment", LocalDateTime.of(2025, 9, 14, 14, 30),
                LocalDateTime.of(2025, 9, 14, 14, 30)));
    }

    /**
     * 获取所有预约
     */
    public List<Appointment> getAllAppointments() {
        logOperation("getAllAppointments", "获取所有预约列表");
        return new ArrayList<>(appointments);
    }

    /**
     * 根据ID查找预约
     */
    public Optional<Appointment> findAppointmentById(String appointmentId) {
        validateNotEmpty(appointmentId, "appointmentId");
        return appointments.stream()
                .filter(apt -> appointmentId.equals(apt.getId()))
                .findFirst();
    }

    /**
     * 根据患者ID获取预约
     */
    public List<Appointment> getAppointmentsByPatientId(String patientId) {
        validateNotEmpty(patientId, "patientId");
        return appointments.stream()
                .filter(apt -> patientId.equals(apt.getPatientId()))
                .collect(Collectors.toList());
    }

    /**
     * 根据医生ID获取预约
     */
    public List<Appointment> getAppointmentsByClinicianId(String clinicianId) {
        validateNotEmpty(clinicianId, "clinicianId");
        return appointments.stream()
                .filter(apt -> clinicianId.equals(apt.getClinicianId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定日期的预约
     */
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        validateNotNull(date, "date");
        return appointments.stream()
                .filter(apt -> date.equals(apt.getAppointmentDate()))
                .collect(Collectors.toList());
    }

    /**
     * 获取今天的预约
     */
    public List<Appointment> getTodayAppointments() {
        return getAppointmentsByDate(LocalDate.now());
    }

    /**
     * 获取即将到来的预约
     */
    public List<Appointment> getUpcomingAppointments() {
        LocalDateTime now = LocalDateTime.now();
        return appointments.stream()
                .filter(Appointment::isUpcoming)
                .sorted((a1, a2) -> a1.getAppointmentDateTime().compareTo(a2.getAppointmentDateTime()))
                .collect(Collectors.toList());
    }

    /**
     * 创建新预约
     */
    public void createAppointment(Appointment appointment) throws HealthcareException {
        validateNotNull(appointment, "appointment");
        if (!appointment.isValid()) {
            throw HealthcareException.validationError("appointment", "预约数据无效");
        }

        // 检查预约ID是否已存在
        if (findAppointmentById(appointment.getId()).isPresent()) {
            throw HealthcareException.businessLogicError("createAppointment", "预约ID已存在: " + appointment.getId());
        }

        // 检查时间冲突
        if (hasTimeConflict(appointment)) {
            throw HealthcareException.businessLogicError("createAppointment", "预约时间存在冲突");
        }

        appointment.setCreatedDate(LocalDateTime.now());
        appointment.setLastModified(LocalDateTime.now());
        appointments.add(appointment);

        logOperation("createAppointment", "创建新预约: " + appointment.getId());
        showMessage("预约创建成功: " + appointment.getAppointmentDateTime());
    }

    /**
     * 更新预约
     */
    public void updateAppointment(Appointment updatedAppointment) throws HealthcareException {
        validateNotNull(updatedAppointment, "updatedAppointment");

        Optional<Appointment> existingAppointment = findAppointmentById(updatedAppointment.getId());
        if (!existingAppointment.isPresent()) {
            throw HealthcareException.businessLogicError("updateAppointment", "预约不存在: " + updatedAppointment.getId());
        }

        if (!updatedAppointment.isValid()) {
            throw HealthcareException.validationError("updatedAppointment", "预约数据无效");
        }

        // 检查时间冲突（排除当前预约）
        if (hasTimeConflict(updatedAppointment, updatedAppointment.getId())) {
            throw HealthcareException.businessLogicError("updateAppointment", "预约时间存在冲突");
        }

        // 更新记录
        appointments.remove(existingAppointment.get());
        updatedAppointment.setLastModified(LocalDateTime.now());
        appointments.add(updatedAppointment);

        logOperation("updateAppointment", "更新预约: " + updatedAppointment.getId());
        showMessage("预约更新成功: " + updatedAppointment.getId());
    }

    /**
     * 取消预约
     */
    public void cancelAppointment(String appointmentId) throws HealthcareException {
        Optional<Appointment> appointment = findAppointmentById(appointmentId);
        if (!appointment.isPresent()) {
            throw HealthcareException.businessLogicError("cancelAppointment", "预约不存在: " + appointmentId);
        }

        if ("Cancelled".equals(appointment.get().getStatus())) {
            throw HealthcareException.businessLogicError("cancelAppointment", "预约已被取消: " + appointmentId);
        }

        appointment.get().setStatus("Cancelled");
        appointment.get().setLastModified(LocalDateTime.now());

        logOperation("cancelAppointment", "取消预约: " + appointmentId);
        showMessage("预约取消成功: " + appointmentId);
    }

    /**
     * 完成预约
     */
    public void completeAppointment(String appointmentId) throws HealthcareException {
        Optional<Appointment> appointment = findAppointmentById(appointmentId);
        if (!appointment.isPresent()) {
            throw HealthcareException.businessLogicError("completeAppointment", "预约不存在: " + appointmentId);
        }

        appointment.get().setStatus("Completed");
        appointment.get().setLastModified(LocalDateTime.now());

        logOperation("completeAppointment", "完成预约: " + appointmentId);
        showMessage("预约完成: " + appointmentId);
    }

    /**
     * 检查时间冲突
     */
    private boolean hasTimeConflict(Appointment newAppointment) {
        return hasTimeConflict(newAppointment, null);
    }

    /**
     * 检查时间冲突（排除指定预约ID）
     */
    private boolean hasTimeConflict(Appointment newAppointment, String excludeAppointmentId) {
        LocalDate date = newAppointment.getAppointmentDate();
        LocalTime startTime = newAppointment.getAppointmentTime();
        LocalTime endTime = newAppointment.getAppointmentEndTime();

        return appointments.stream()
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
    public String getAppointmentStatistics() {
        int total = appointments.size();
        long scheduled = appointments.stream().filter(Appointment::isScheduled).count();
        long completed = appointments.stream().filter(Appointment::isCompleted).count();
        long cancelled = appointments.stream().filter(Appointment::isCancelled).count();
        long today = getTodayAppointments().size();
        long upcoming = getUpcomingAppointments().size();

        return String.format("预约统计:\n总预约数: %d\n已预约: %d\n已完成: %d\n已取消: %d\n今日预约: %d\n即将到来: %d",
                total, scheduled, completed, cancelled, today, upcoming);
    }

    /**
     * 获取预约数量
     */
    public int getAppointmentCount() {
        return appointments.size();
    }

    @Override
    public void initialize() {
        logOperation("initialize", "预约控制器初始化完成");
    }

    @Override
    public void cleanup() {
        logOperation("cleanup", "预约控制器清理完成");
        appointments.clear();
    }
}
