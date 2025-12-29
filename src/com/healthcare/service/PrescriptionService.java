package com.healthcare.service;

import com.healthcare.model.Prescription;
import com.healthcare.util.HealthcareException;

import java.time.LocalDate;
import java.util.List;

/**
 * 处方服务
 * 处理处方相关的复杂业务逻辑
 *
 * @author Healthcare System
 * @version 1.0
 */
public class PrescriptionService extends BaseService {

    @Override
    public void initialize() throws HealthcareException {
        logServiceEvent("正在初始化处方服务...");
        // 验证必要的Repository
        validateParameter(repositoryManager.getPrescriptionRepository(), "PrescriptionRepository");
        validateParameter(repositoryManager.getPatientRepository(), "PatientRepository");
        validateParameter(repositoryManager.getClinicianRepository(), "ClinicianRepository");
        validateParameter(repositoryManager.getAppointmentRepository(), "AppointmentRepository");
        initialized = true;
        logServiceEvent("处方服务初始化完成");
    }

    /**
     * 开具处方
     */
    public Prescription prescribeMedication(String patientId, String clinicianId, String medicationName,
                                          String dosage, String frequency, int durationDays, int quantity,
                                          String instructions, String pharmacyName) throws HealthcareException {
        validateInitialized();
        validateStringParameter(patientId, "patientId");
        validateStringParameter(clinicianId, "clinicianId");
        validateStringParameter(medicationName, "medicationName");
        validateStringParameter(dosage, "dosage");
        validateStringParameter(frequency, "frequency");

        return executeOperation("prescribeMedication", () -> {
            try {
                // 验证患者和医生存在
                validateBusinessRule(repositoryManager.getPatientRepository().existsById(patientId),
                                   "患者不存在: " + patientId);
                validateBusinessRule(repositoryManager.getClinicianRepository().existsById(clinicianId),
                                   "医生不存在: " + clinicianId);

                // 生成处方ID
                String prescriptionId = generatePrescriptionId();

                // 创建处方
                Prescription prescription = new Prescription(
                    prescriptionId, patientId, clinicianId, null, // appointmentId can be null
                    LocalDate.now(), medicationName, dosage, frequency, durationDays, quantity,
                    instructions, pharmacyName, "Issued", LocalDate.now(), null
                );

                // 验证处方数据
                validatePrescriptionData(prescription);

                // 保存处方
                repositoryManager.getPrescriptionRepository().save(prescription);
                repositoryManager.flushAllData();

                logServiceEvent("处方开具成功: " + prescriptionId + " - " + medicationName);
                return prescription;

            } catch (HealthcareException e) {
                logServiceEvent("处方开具失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 关联处方到预约
     */
    public void linkPrescriptionToAppointment(String prescriptionId, String appointmentId) throws HealthcareException {
        validateInitialized();
        validateStringParameter(prescriptionId, "prescriptionId");
        validateStringParameter(appointmentId, "appointmentId");

        recordOperation("linkPrescriptionToAppointment", () -> {
            try {
                // 验证处方和预约存在
                Optional<Prescription> prescriptionOpt = repositoryManager.getPrescriptionRepository().findById(prescriptionId);
                if (!prescriptionOpt.isPresent()) {
                    throw new HealthcareException("处方不存在: " + prescriptionId);
                }

                validateBusinessRule(repositoryManager.getAppointmentRepository().existsById(appointmentId),
                                   "预约不存在: " + appointmentId);

                Prescription prescription = prescriptionOpt.get();
                prescription.setAppointmentId(appointmentId);

                repositoryManager.getPrescriptionRepository().save(prescription);
                repositoryManager.flushAllData();

                logServiceEvent("处方关联成功: " + prescriptionId + " -> " + appointmentId);

            } catch (HealthcareException e) {
                logServiceEvent("处方关联失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 处方到药
     */
    public void dispensePrescription(String prescriptionId) throws HealthcareException {
        validateInitialized();
        validateStringParameter(prescriptionId, "prescriptionId");

        recordOperation("dispensePrescription", () -> {
            try {
                Optional<Prescription> prescriptionOpt = repositoryManager.getPrescriptionRepository().findById(prescriptionId);
                if (!prescriptionOpt.isPresent()) {
                    throw new HealthcareException("处方不存在: " + prescriptionId);
                }

                Prescription prescription = prescriptionOpt.get();

                // 验证处方状态
                validateBusinessRule(prescription.isIssued(), "处方未发放，无法到药: " + prescriptionId);
                validateBusinessRule(!prescription.isCollected(), "处方已到药: " + prescriptionId);
                validateBusinessRule(!prescription.isExpired(), "处方已过期: " + prescriptionId);

                prescription.setStatus("Collected");
                prescription.setCollectionDate(LocalDate.now());

                repositoryManager.getPrescriptionRepository().save(prescription);
                repositoryManager.flushAllData();

                logServiceEvent("处方到药成功: " + prescriptionId);

            } catch (HealthcareException e) {
                logServiceEvent("处方到药失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 取消处方
     */
    public void cancelPrescription(String prescriptionId, String reason) throws HealthcareException {
        validateInitialized();
        validateStringParameter(prescriptionId, "prescriptionId");

        recordOperation("cancelPrescription", () -> {
            try {
                Optional<Prescription> prescriptionOpt = repositoryManager.getPrescriptionRepository().findById(prescriptionId);
                if (!prescriptionOpt.isPresent()) {
                    throw new HealthcareException("处方不存在: " + prescriptionId);
                }

                Prescription prescription = prescriptionOpt.get();

                // 验证是否可以取消
                validateBusinessRule(!prescription.isCollected(), "已到药的处方不能取消: " + prescriptionId);

                prescription.setStatus("Cancelled");
                prescription.setInstructions((prescription.getInstructions() != null ? prescription.getInstructions() + "; " : "") +
                                           "取消原因: " + (reason != null ? reason : "医生取消"));

                repositoryManager.getPrescriptionRepository().save(prescription);
                repositoryManager.flushAllData();

                logServiceEvent("处方取消成功: " + prescriptionId);

            } catch (HealthcareException e) {
                logServiceEvent("处方取消失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 续方
     */
    public Prescription renewPrescription(String originalPrescriptionId, int newDurationDays)
            throws HealthcareException {
        validateInitialized();
        validateStringParameter(originalPrescriptionId, "originalPrescriptionId");

        return executeOperation("renewPrescription", () -> {
            try {
                Optional<Prescription> originalOpt = repositoryManager.getPrescriptionRepository().findById(originalPrescriptionId);
                if (!originalOpt.isPresent()) {
                    throw new HealthcareException("原处方不存在: " + originalPrescriptionId);
                }

                Prescription original = originalOpt.get();

                // 生成新处方ID
                String newPrescriptionId = generatePrescriptionId();

                // 创建续方
                Prescription renewedPrescription = new Prescription(
                    newPrescriptionId, original.getPatientId(), original.getClinicianId(),
                    original.getAppointmentId(), LocalDate.now(), original.getMedicationName(),
                    original.getDosage(), original.getFrequency(), newDurationDays,
                    calculateQuantity(original.getDosage(), original.getFrequency(), newDurationDays),
                    "续方 - " + (original.getInstructions() != null ? original.getInstructions() : ""),
                    original.getPharmacyName(), "Issued", LocalDate.now(), null
                );

                // 验证新处方
                validatePrescriptionData(renewedPrescription);

                repositoryManager.getPrescriptionRepository().save(renewedPrescription);
                repositoryManager.flushAllData();

                logServiceEvent("处方续方成功: " + originalPrescriptionId + " -> " + newPrescriptionId);
                return renewedPrescription;

            } catch (HealthcareException e) {
                logServiceEvent("处方续方失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 获取患者活跃处方
     */
    public List<Prescription> getActivePrescriptions(String patientId) throws HealthcareException {
        validateInitialized();
        validateStringParameter(patientId, "patientId");

        return executeOperation("getActivePrescriptions", () -> {
            try {
                return repositoryManager.getPrescriptionRepository().findByPatientId(patientId).stream()
                    .filter(rx -> rx.isIssued() && !rx.isCollected() && !rx.isExpired())
                    .collect(java.util.stream.Collectors.toList());

            } catch (Exception e) {
                logServiceEvent("获取活跃处方失败: " + e.getMessage());
                throw new HealthcareException("获取活跃处方失败", e);
            }
        });
    }

    /**
     * 验证处方数据
     */
    private void validatePrescriptionData(Prescription prescription) throws HealthcareException {
        validateBusinessRule(prescription.isValid(), "处方数据不符合要求");
        validateBusinessRule(prescription.getDurationDays() > 0 && prescription.getDurationDays() <= 365,
                           "处方有效期必须在1-365天之间");
        validateBusinessRule(prescription.getQuantity() > 0, "处方数量必须大于0");
    }

    /**
     * 计算处方数量
     */
    private int calculateQuantity(String dosage, String frequency, int durationDays) {
        // 简单的数量计算逻辑（可以根据具体需求调整）
        try {
            // 解析频率（如"twice daily" -> 2次/天）
            int dailyFrequency = 1; // 默认1次/天
            if (frequency != null) {
                String freq = frequency.toLowerCase();
                if (freq.contains("twice") || freq.contains("two")) dailyFrequency = 2;
                else if (freq.contains("three")) dailyFrequency = 3;
                else if (freq.contains("four")) dailyFrequency = 4;
            }

            return dailyFrequency * durationDays;
        } catch (Exception e) {
            // 如果解析失败，返回安全值
            return Math.max(1, durationDays);
        }
    }

    /**
     * 生成处方ID
     */
    private String generatePrescriptionId() {
        return "RX" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    /**
     * 获取处方过期提醒
     */
    public List<Prescription> getPrescriptionExpiryAlerts() throws HealthcareException {
        validateInitialized();

        return executeOperation("getPrescriptionExpiryAlerts", () -> {
            try {
                return repositoryManager.getPrescriptionRepository().findExpiringPrescriptions();

            } catch (Exception e) {
                logServiceEvent("获取处方过期提醒失败: " + e.getMessage());
                throw new HealthcareException("获取处方过期提醒失败", e);
            }
        });
    }

    @Override
    public String getServiceStatus() {
        return getBasicStatus() +
               ", 处方总数: " + (initialized ?
                   repositoryManager.getPrescriptionRepository().getCacheSize() : "未知");
    }

    @Override
    public String getStatistics() {
        if (!initialized) {
            return "服务未初始化";
        }

        try {
            StringBuilder stats = new StringBuilder();
            stats.append("处方服务统计:\n");
            stats.append("- 处方总数: ").append(repositoryManager.getPrescriptionRepository().count()).append("\n");
            stats.append(repositoryManager.getPrescriptionRepository().getPrescriptionStatistics());
            return stats.toString();
        } catch (Exception e) {
            return "获取统计信息失败: " + e.getMessage();
        }
    }
}
