package com.healthcare.service;

import com.healthcare.model.Patient;
import com.healthcare.util.HealthcareException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 患者服务
 * 处理患者相关的复杂业务逻辑
 *
 * @author Healthcare System
 * @version 1.0
 */
public class PatientService extends BaseService {

    @Override
    public void initialize() throws HealthcareException {
        logServiceEvent("正在初始化患者服务...");
        // 验证必要的Repository
        validateParameter(repositoryManager.getPatientRepository(), "PatientRepository");
        validateParameter(repositoryManager.getFacilityRepository(), "FacilityRepository");
        initialized = true;
        logServiceEvent("患者服务初始化完成");
    }

    /**
     * 注册新患者
     */
    public Patient registerPatient(Patient patient) throws HealthcareException {
        validateInitialized();
        validateParameter(patient, "patient");

        return executeOperation("registerPatient", () -> {
            try {
                // 验证患者数据
                validatePatientData(patient);

                // 检查NHS号码是否已存在
                if (repositoryManager.getPatientRepository().findByNhsNumber(patient.getNhsNumber()).isPresent()) {
                    throw new HealthcareException("NHS号码已存在: " + patient.getNhsNumber());
                }

                // 检查GP诊所是否存在
                if (patient.getGpSurgeryId() != null &&
                    !repositoryManager.getFacilityRepository().existsById(patient.getGpSurgeryId())) {
                    throw new HealthcareException("指定的GP诊所不存在: " + patient.getGpSurgeryId());
                }

                // 设置注册日期
                patient.setRegistrationDate(LocalDate.now());

                // 保存患者
                repositoryManager.getPatientRepository().save(patient);
                repositoryManager.flushAllData();

                logServiceEvent("患者注册成功: " + patient.getFullName());
                return patient;

            } catch (HealthcareException e) {
                logServiceEvent("患者注册失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 更新患者信息
     */
    public Patient updatePatient(Patient patient) throws HealthcareException {
        validateInitialized();
        validateParameter(patient, "patient");

        return executeOperation("updatePatient", () -> {
            try {
                // 验证患者是否存在
                Optional<Patient> existingPatient = repositoryManager.getPatientRepository().findById(patient.getId());
                if (!existingPatient.isPresent()) {
                    throw new HealthcareException("患者不存在: " + patient.getId());
                }

                // 验证患者数据
                validatePatientData(patient);

                // 检查GP诊所是否存在
                if (patient.getGpSurgeryId() != null &&
                    !repositoryManager.getFacilityRepository().existsById(patient.getGpSurgeryId())) {
                    throw new HealthcareException("指定的GP诊所不存在: " + patient.getGpSurgeryId());
                }

                // 保存更新
                repositoryManager.getPatientRepository().save(patient);
                repositoryManager.flushAllData();

                logServiceEvent("患者信息更新成功: " + patient.getFullName());
                return patient;

            } catch (HealthcareException e) {
                logServiceEvent("患者信息更新失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 注销患者
     */
    public void deregisterPatient(String patientId) throws HealthcareException {
        validateInitialized();
        validateStringParameter(patientId, "patientId");

        recordOperation("deregisterPatient", () -> {
            try {
                // 检查患者是否存在
                Optional<Patient> patient = repositoryManager.getPatientRepository().findById(patientId);
                if (!patient.isPresent()) {
                    throw new HealthcareException("患者不存在: " + patientId);
                }

                // 检查是否有未完成的预约
                List<?> activeAppointments = repositoryManager.getAppointmentRepository()
                    .findAll().stream()
                    .filter(apt -> patientId.equals(apt.getPatientId()) &&
                            ("Scheduled".equals(apt.getStatus()) || "Confirmed".equals(apt.getStatus())))
                    .toList();

                if (!activeAppointments.isEmpty()) {
                    throw new HealthcareException("患者有未完成的预约，无法注销");
                }

                // 删除患者
                repositoryManager.getPatientRepository().deleteById(patientId);
                repositoryManager.flushAllData();

                logServiceEvent("患者注销成功: " + patient.get().getFullName());

            } catch (HealthcareException e) {
                logServiceEvent("患者注销失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 转移患者到其他GP诊所
     */
    public Patient transferPatient(String patientId, String newGpSurgeryId) throws HealthcareException {
        validateInitialized();
        validateStringParameter(patientId, "patientId");
        validateStringParameter(newGpSurgeryId, "newGpSurgeryId");

        return executeOperation("transferPatient", () -> {
            try {
                // 验证患者是否存在
                Optional<Patient> patientOpt = repositoryManager.getPatientRepository().findById(patientId);
                if (!patientOpt.isPresent()) {
                    throw new HealthcareException("患者不存在: " + patientId);
                }

                // 验证新GP诊所是否存在
                if (!repositoryManager.getFacilityRepository().existsById(newGpSurgeryId)) {
                    throw new HealthcareException("目标GP诊所不存在: " + newGpSurgeryId);
                }

                Patient patient = patientOpt.get();

                // 检查是否已经是同一个诊所
                if (newGpSurgeryId.equals(patient.getGpSurgeryId())) {
                    throw new HealthcareException("患者已经在目标诊所中");
                }

                patient.setGpSurgeryId(newGpSurgeryId);

                // 保存更改
                repositoryManager.getPatientRepository().save(patient);
                repositoryManager.flushAllData();

                logServiceEvent("患者转移成功: " + patient.getFullName() + " -> " + newGpSurgeryId);
                return patient;

            } catch (HealthcareException e) {
                logServiceEvent("患者转移失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 获取患者完整档案
     */
    public PatientRecord getPatientRecord(String patientId) throws HealthcareException {
        validateInitialized();
        validateStringParameter(patientId, "patientId");

        return executeOperation("getPatientRecord", () -> {
            try {
                // 获取患者基本信息
                Optional<Patient> patientOpt = repositoryManager.getPatientRepository().findById(patientId);
                if (!patientOpt.isPresent()) {
                    throw new HealthcareException("患者不存在: " + patientId);
                }

                Patient patient = patientOpt.get();

                // 获取相关预约
                List<?> appointments = repositoryManager.getAppointmentRepository()
                    .findByPatientId(patientId);

                // 获取相关处方
                List<?> prescriptions = repositoryManager.getPrescriptionRepository()
                    .findByPatientId(patientId);

                // 获取相关转诊
                List<?> referrals = repositoryManager.getReferralRepository()
                    .findByPatientId(patientId);

                return new PatientRecord(patient, appointments, prescriptions, referrals);

            } catch (HealthcareException e) {
                logServiceEvent("获取患者档案失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 验证患者数据
     */
    private void validatePatientData(Patient patient) throws HealthcareException {
        validateBusinessRule(patient.isValid(), "患者数据不符合要求");
        validateBusinessRule(patient.getDateOfBirth() != null, "出生日期不能为空");
        validateBusinessRule(patient.getDateOfBirth().isBefore(LocalDate.now()), "出生日期不能是未来日期");
        validateBusinessRule(patient.getAge() >= 0 && patient.getAge() <= 150, "患者年龄不合理");
    }

    /**
     * 生成患者报告
     */
    public String generatePatientReport(String patientId) throws HealthcareException {
        PatientRecord record = getPatientRecord(patientId);

        StringBuilder report = new StringBuilder();
        report.append("=== 患者医疗档案报告 ===\n\n");

        Patient patient = record.getPatient();
        report.append("基本信息:\n");
        report.append("- 姓名: ").append(patient.getFullName()).append("\n");
        report.append("- NHS号码: ").append(patient.getNhsNumber()).append("\n");
        report.append("- 出生日期: ").append(patient.getDateOfBirth()).append("\n");
        report.append("- 年龄: ").append(patient.getAge()).append("岁\n");
        report.append("- 联系电话: ").append(patient.getPhoneNumber()).append("\n");
        report.append("- 邮箱: ").append(patient.getEmail()).append("\n");
        report.append("- 地址: ").append(patient.getAddress()).append("\n");
        report.append("- 注册GP诊所: ").append(patient.getGpSurgeryId()).append("\n\n");

        report.append("医疗记录统计:\n");
        report.append("- 预约次数: ").append(record.getAppointments().size()).append("\n");
        report.append("- 处方数量: ").append(record.getPrescriptions().size()).append("\n");
        report.append("- 转诊记录: ").append(record.getReferrals().size()).append("\n");

        report.append("\n=== 报告生成完成 ===\n");
        return report.toString();
    }

    @Override
    public String getServiceStatus() {
        return getBasicStatus() +
               ", 患者总数: " + (initialized ?
                   repositoryManager.getPatientRepository().getCacheSize() : "未知");
    }

    @Override
    public String getStatistics() {
        if (!initialized) {
            return "服务未初始化";
        }

        try {
            StringBuilder stats = new StringBuilder();
            stats.append("患者服务统计:\n");
            stats.append("- 患者总数: ").append(repositoryManager.getPatientRepository().count()).append("\n");
            stats.append("- 年龄分布: \n").append(repositoryManager.getPatientRepository().getAgeStatistics());
            return stats.toString();
        } catch (Exception e) {
            return "获取统计信息失败: " + e.getMessage();
        }
    }

    /**
     * 患者档案内部类
     */
    public static class PatientRecord {
        private final Patient patient;
        private final List<?> appointments;
        private final List<?> prescriptions;
        private final List<?> referrals;

        public PatientRecord(Patient patient, List<?> appointments, List<?> prescriptions, List<?> referrals) {
            this.patient = patient;
            this.appointments = appointments;
            this.prescriptions = prescriptions;
            this.referrals = referrals;
        }

        public Patient getPatient() { return patient; }
        public List<?> getAppointments() { return appointments; }
        public List<?> getPrescriptions() { return prescriptions; }
        public List<?> getReferrals() { return referrals; }
    }
}
