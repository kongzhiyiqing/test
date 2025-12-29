package com.healthcare.controller;

import com.healthcare.model.Referral;
import com.healthcare.util.HealthcareException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 转诊管理控制器
 * 处理转诊相关的业务逻辑
 *
 * @author Healthcare System
 * @version 1.0
 */
public class ReferralController extends BaseController {

    private List<Referral> referrals;

    /**
     * 构造函数
     */
    public ReferralController() {
        this.referrals = new ArrayList<>();
        initializeSampleData();
    }

    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        referrals.add(new Referral("R001", "P005", "C001", "C005", "S001", "H001",
                LocalDate.of(2025, 9, 10), "Routine", "Heart murmur investigation",
                "35-year-old male with Grade 2/6 systolic murmur detected during routine examination. No symptoms of chest pain or breathlessness.",
                "Echocardiogram|ECG", "Completed", "A005",
                "Patient seen, echo normal", LocalDate.of(2025, 9, 10), LocalDate.of(2025, 9, 25)));

        referrals.add(new Referral("R002", "P001", "C001", "C006", "S001", "H001",
                LocalDate.of(2025, 9, 20), "Urgent", "Persistent headaches",
                "40-year-old male with 3-month history of severe morning headaches. No focal neurological signs but concerned about intracranial pressure.",
                "MRI Brain|Neurological assessment", "Pending", null,
                "Awaiting MRI results", LocalDate.of(2025, 9, 20), LocalDate.of(2025, 9, 20)));
    }

    /**
     * 获取所有转诊
     */
    public List<Referral> getAllReferrals() {
        logOperation("getAllReferrals", "获取所有转诊列表");
        return new ArrayList<>(referrals);
    }

    /**
     * 根据ID查找转诊
     */
    public Optional<Referral> findReferralById(String referralId) {
        validateNotEmpty(referralId, "referralId");
        return referrals.stream()
                .filter(ref -> referralId.equals(ref.getId()))
                .findFirst();
    }

    /**
     * 根据患者ID获取转诊
     */
    public List<Referral> getReferralsByPatientId(String patientId) {
        validateNotEmpty(patientId, "patientId");
        return referrals.stream()
                .filter(ref -> patientId.equals(ref.getPatientId()))
                .collect(Collectors.toList());
    }

    /**
     * 根据转诊医生ID获取转诊
     */
    public List<Referral> getReferralsByReferringClinicianId(String clinicianId) {
        validateNotEmpty(clinicianId, "clinicianId");
        return referrals.stream()
                .filter(ref -> clinicianId.equals(ref.getReferringClinicianId()))
                .collect(Collectors.toList());
    }

    /**
     * 根据接收医生ID获取转诊
     */
    public List<Referral> getReferralsByReferredClinicianId(String clinicianId) {
        validateNotEmpty(clinicianId, "clinicianId");
        return referrals.stream()
                .filter(ref -> clinicianId.equals(ref.getReferredToClinicianId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取紧急转诊
     */
    public List<Referral> getUrgentReferrals() {
        return referrals.stream()
                .filter(Referral::isUrgent)
                .collect(Collectors.toList());
    }

    /**
     * 获取常规转诊
     */
    public List<Referral> getRoutineReferrals() {
        return referrals.stream()
                .filter(Referral::isRoutine)
                .collect(Collectors.toList());
    }

    /**
     * 获取已完成的转诊
     */
    public List<Referral> getCompletedReferrals() {
        return referrals.stream()
                .filter(Referral::isCompleted)
                .collect(Collectors.toList());
    }

    /**
     * 获取进行中的转诊
     */
    public List<Referral> getInProgressReferrals() {
        return referrals.stream()
                .filter(Referral::isInProgress)
                .collect(Collectors.toList());
    }

    /**
     * 获取新的转诊
     */
    public List<Referral> getNewReferrals() {
        return referrals.stream()
                .filter(Referral::isNew)
                .collect(Collectors.toList());
    }

    /**
     * 获取过期的转诊
     */
    public List<Referral> getOverdueReferrals() {
        return referrals.stream()
                .filter(Referral::isOverdue)
                .collect(Collectors.toList());
    }

    /**
     * 创建新转诊
     */
    public void createReferral(Referral referral) throws HealthcareException {
        validateNotNull(referral, "referral");
        if (!referral.isValid()) {
            throw HealthcareException.validationError("referral", "转诊数据无效");
        }

        // 检查转诊ID是否已存在
        if (findReferralById(referral.getId()).isPresent()) {
            throw HealthcareException.businessLogicError("createReferral", "转诊ID已存在: " + referral.getId());
        }

        referral.setCreatedDate(LocalDate.now());
        referral.setLastUpdated(LocalDate.now());
        referrals.add(referral);

        logOperation("createReferral", "创建新转诊: " + referral.getId());
        showMessage("转诊创建成功: " + referral.getId());
    }

    /**
     * 更新转诊
     */
    public void updateReferral(Referral updatedReferral) throws HealthcareException {
        validateNotNull(updatedReferral, "updatedReferral");

        Optional<Referral> existingReferral = findReferralById(updatedReferral.getId());
        if (!existingReferral.isPresent()) {
            throw HealthcareException.businessLogicError("updateReferral", "转诊不存在: " + updatedReferral.getId());
        }

        if (!updatedReferral.isValid()) {
            throw HealthcareException.validationError("updatedReferral", "转诊数据无效");
        }

        referrals.remove(existingReferral.get());
        updatedReferral.setLastUpdated(LocalDate.now());
        referrals.add(updatedReferral);

        logOperation("updateReferral", "更新转诊: " + updatedReferral.getId());
        showMessage("转诊更新成功: " + updatedReferral.getId());
    }

    /**
     * 完成转诊
     */
    public void completeReferral(String referralId, String notes) throws HealthcareException {
        Optional<Referral> referral = findReferralById(referralId);
        if (!referral.isPresent()) {
            throw HealthcareException.businessLogicError("completeReferral", "转诊不存在: " + referralId);
        }

        referral.get().setStatus("Completed");
        if (notes != null && !notes.trim().isEmpty()) {
            referral.get().setNotes(notes);
        }
        referral.get().setLastUpdated(LocalDate.now());

        logOperation("completeReferral", "完成转诊: " + referralId);
        showMessage("转诊完成: " + referralId);
    }

    /**
     * 更新转诊状态为进行中
     */
    public void startReferral(String referralId) throws HealthcareException {
        Optional<Referral> referral = findReferralById(referralId);
        if (!referral.isPresent()) {
            throw HealthcareException.businessLogicError("startReferral", "转诊不存在: " + referralId);
        }

        if (!"New".equals(referral.get().getStatus())) {
            throw HealthcareException.businessLogicError("startReferral", "只能开始新转诊: " + referralId);
        }

        referral.get().setStatus("In Progress");
        referral.get().setLastUpdated(LocalDate.now());

        logOperation("startReferral", "开始处理转诊: " + referralId);
        showMessage("转诊开始处理: " + referralId);
    }

    /**
     * 关联预约到转诊
     */
    public void linkAppointmentToReferral(String referralId, String appointmentId) throws HealthcareException {
        Optional<Referral> referral = findReferralById(referralId);
        if (!referral.isPresent()) {
            throw HealthcareException.businessLogicError("linkAppointmentToReferral", "转诊不存在: " + referralId);
        }

        referral.get().setAppointmentId(appointmentId);
        referral.get().setLastUpdated(LocalDate.now());

        logOperation("linkAppointmentToReferral", "关联预约到转诊: " + referralId + " -> " + appointmentId);
        showMessage("预约关联成功: " + appointmentId);
    }

    /**
     * 获取转诊统计信息
     */
    public String getReferralStatistics() {
        int total = referrals.size();
        long urgent = referrals.stream().filter(Referral::isUrgent).count();
        long routine = referrals.stream().filter(Referral::isRoutine).count();
        long completed = referrals.stream().filter(Referral::isCompleted).count();
        long inProgress = referrals.stream().filter(Referral::isInProgress).count();
        long newReferrals = referrals.stream().filter(Referral::isNew).count();
        long overdue = referrals.stream().filter(Referral::isOverdue).count();

        return String.format("转诊统计:\n总转诊数: %d\n紧急转诊: %d\n常规转诊: %d\n已完成: %d\n进行中: %d\n新转诊: %d\n已过期: %d",
                total, urgent, routine, completed, inProgress, newReferrals, overdue);
    }

    /**
     * 获取转诊数量
     */
    public int getReferralCount() {
        return referrals.size();
    }

    @Override
    public void initialize() {
        logOperation("initialize", "转诊控制器初始化完成");
    }

    @Override
    public void cleanup() {
        logOperation("cleanup", "转诊控制器清理完成");
        referrals.clear();
    }
}
