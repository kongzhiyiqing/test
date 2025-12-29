package com.healthcare.controller;

import com.healthcare.model.Prescription;
import com.healthcare.util.HealthcareException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 处方管理控制器
 * 处理处方相关的业务逻辑
 *
 * @author Healthcare System
 * @version 1.0
 */
public class PrescriptionController extends BaseController {

    private List<Prescription> prescriptions;

    /**
     * 构造函数
     */
    public PrescriptionController() {
        this.prescriptions = new ArrayList<>();
        initializeSampleData();
    }

    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        prescriptions.add(new Prescription("RX001", "P001", "C001", "A001",
                LocalDate.of(2025, 9, 20), "Simvastatin", "20mg", "Once daily", 28, 28,
                "Take with evening meal", "Boots Pharmacy Birmingham", "Issued",
                LocalDate.of(2025, 9, 20), null));

        prescriptions.add(new Prescription("RX002", "P002", "C009", "A002",
                LocalDate.of(2025, 9, 20), "Paracetamol", "500mg", "As required", 7, 16,
                "Max 8 tablets in 24 hours", "Superdrug Birmingham Central", "Issued",
                LocalDate.of(2025, 9, 20), null));
    }

    /**
     * 获取所有处方
     */
    public List<Prescription> getAllPrescriptions() {
        logOperation("getAllPrescriptions", "获取所有处方列表");
        return new ArrayList<>(prescriptions);
    }

    /**
     * 根据ID查找处方
     */
    public Optional<Prescription> findPrescriptionById(String prescriptionId) {
        validateNotEmpty(prescriptionId, "prescriptionId");
        return prescriptions.stream()
                .filter(rx -> prescriptionId.equals(rx.getId()))
                .findFirst();
    }

    /**
     * 根据患者ID获取处方
     */
    public List<Prescription> getPrescriptionsByPatientId(String patientId) {
        validateNotEmpty(patientId, "patientId");
        return prescriptions.stream()
                .filter(rx -> patientId.equals(rx.getPatientId()))
                .collect(Collectors.toList());
    }

    /**
     * 根据医生ID获取处方
     */
    public List<Prescription> getPrescriptionsByClinicianId(String clinicianId) {
        validateNotEmpty(clinicianId, "clinicianId");
        return prescriptions.stream()
                .filter(rx -> clinicianId.equals(rx.getClinicianId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取已发放的处方
     */
    public List<Prescription> getIssuedPrescriptions() {
        return prescriptions.stream()
                .filter(Prescription::isIssued)
                .collect(Collectors.toList());
    }

    /**
     * 获取已收集的处方
     */
    public List<Prescription> getCollectedPrescriptions() {
        return prescriptions.stream()
                .filter(Prescription::isCollected)
                .collect(Collectors.toList());
    }

    /**
     * 获取过期的处方
     */
    public List<Prescription> getExpiredPrescriptions() {
        return prescriptions.stream()
                .filter(Prescription::isExpired)
                .collect(Collectors.toList());
    }

    /**
     * 创建新处方
     */
    public void createPrescription(Prescription prescription) throws HealthcareException {
        validateNotNull(prescription, "prescription");
        if (!prescription.isValid()) {
            throw HealthcareException.validationError("prescription", "处方数据无效");
        }

        // 检查处方ID是否已存在
        if (findPrescriptionById(prescription.getId()).isPresent()) {
            throw HealthcareException.businessLogicError("createPrescription", "处方ID已存在: " + prescription.getId());
        }

        prescription.setIssueDate(LocalDate.now());
        prescriptions.add(prescription);

        logOperation("createPrescription", "创建新处方: " + prescription.getId());
        showMessage("处方创建成功: " + prescription.getMedicationName());
    }

    /**
     * 更新处方
     */
    public void updatePrescription(Prescription updatedPrescription) throws HealthcareException {
        validateNotNull(updatedPrescription, "updatedPrescription");

        Optional<Prescription> existingPrescription = findPrescriptionById(updatedPrescription.getId());
        if (!existingPrescription.isPresent()) {
            throw HealthcareException.businessLogicError("updatePrescription", "处方不存在: " + updatedPrescription.getId());
        }

        if (!updatedPrescription.isValid()) {
            throw HealthcareException.validationError("updatedPrescription", "处方数据无效");
        }

        prescriptions.remove(existingPrescription.get());
        prescriptions.add(updatedPrescription);

        logOperation("updatePrescription", "更新处方: " + updatedPrescription.getId());
        showMessage("处方更新成功: " + updatedPrescription.getId());
    }

    /**
     * 收集处方（标记为已收集）
     */
    public void collectPrescription(String prescriptionId) throws HealthcareException {
        Optional<Prescription> prescription = findPrescriptionById(prescriptionId);
        if (!prescription.isPresent()) {
            throw HealthcareException.businessLogicError("collectPrescription", "处方不存在: " + prescriptionId);
        }

        if (!prescription.get().isIssued()) {
            throw HealthcareException.businessLogicError("collectPrescription", "处方尚未发放: " + prescriptionId);
        }

        if (prescription.get().isCollected()) {
            throw HealthcareException.businessLogicError("collectPrescription", "处方已被收集: " + prescriptionId);
        }

        prescription.get().setStatus("Collected");
        prescription.get().setCollectionDate(LocalDate.now());

        logOperation("collectPrescription", "处方已收集: " + prescriptionId);
        showMessage("处方收集成功: " + prescriptionId);
    }

    /**
     * 发放处方
     */
    public void issuePrescription(String prescriptionId) throws HealthcareException {
        Optional<Prescription> prescription = findPrescriptionById(prescriptionId);
        if (!prescription.isPresent()) {
            throw HealthcareException.businessLogicError("issuePrescription", "处方不存在: " + prescriptionId);
        }

        prescription.get().setStatus("Issued");
        prescription.get().setIssueDate(LocalDate.now());

        logOperation("issuePrescription", "处方已发放: " + prescriptionId);
        showMessage("处方发放成功: " + prescriptionId);
    }

    /**
     * 搜索处方（按药物名称）
     */
    public List<Prescription> searchPrescriptionsByMedication(String medicationName) {
        if (medicationName == null || medicationName.trim().isEmpty()) {
            return getAllPrescriptions();
        }

        String searchTerm = medicationName.toLowerCase().trim();
        return prescriptions.stream()
                .filter(rx -> rx.getMedicationName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * 获取即将到期的处方（7天内）
     */
    public List<Prescription> getExpiringPrescriptions() {
        LocalDate weekFromNow = LocalDate.now().plusDays(7);
        return prescriptions.stream()
                .filter(rx -> rx.isIssued() && !rx.isCollected())
                .filter(rx -> {
                    LocalDate expiryDate = rx.getExpiryDate();
                    return expiryDate != null && !expiryDate.isAfter(weekFromNow);
                })
                .sorted((rx1, rx2) -> rx1.getExpiryDate().compareTo(rx2.getExpiryDate()))
                .collect(Collectors.toList());
    }

    /**
     * 获取处方统计信息
     */
    public String getPrescriptionStatistics() {
        int total = prescriptions.size();
        long issued = prescriptions.stream().filter(Prescription::isIssued).count();
        long collected = prescriptions.stream().filter(Prescription::isCollected).count();
        long expired = prescriptions.stream().filter(Prescription::isExpired).count();
        long expiringSoon = getExpiringPrescriptions().size();

        return String.format("处方统计:\n总处方数: %d\n已发放: %d\n已收集: %d\n已过期: %d\n即将过期: %d",
                total, issued, collected, expired, expiringSoon);
    }

    /**
     * 获取处方数量
     */
    public int getPrescriptionCount() {
        return prescriptions.size();
    }

    @Override
    public void initialize() {
        logOperation("initialize", "处方控制器初始化完成");
    }

    @Override
    public void cleanup() {
        logOperation("cleanup", "处方控制器清理完成");
        prescriptions.clear();
    }
}
