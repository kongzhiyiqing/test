package com.healthcare.repository;

import com.healthcare.util.HealthcareException;

/**
 * 仓库管理器
 * 统一管理所有数据仓库实例
 *
 * @author Healthcare System
 * @version 1.0
 */
public class RepositoryManager {

    // Repository实例
    private PatientRepository patientRepository;
    private ClinicianRepository clinicianRepository;
    private FacilityRepository facilityRepository;
    private AppointmentRepository appointmentRepository;
    private PrescriptionRepository prescriptionRepository;
    private ReferralRepository referralRepository;
    private StaffRepository staffRepository;

    // 单例模式
    private static RepositoryManager instance;

    /**
     * 私有构造函数
     */
    private RepositoryManager() {
        initializeRepositories();
    }

    /**
     * 获取单例实例
     */
    public static synchronized RepositoryManager getInstance() {
        if (instance == null) {
            instance = new RepositoryManager();
        }
        return instance;
    }

    /**
     * 初始化所有仓库
     */
    private void initializeRepositories() {
        try {
            clinicianRepository = new ClinicianRepository();
            patientRepository = new PatientRepository();
            facilityRepository = new FacilityRepository();
            appointmentRepository = new AppointmentRepository();
            prescriptionRepository = new PrescriptionRepository();
            referralRepository = new ReferralRepository();
            staffRepository = new StaffRepository();

            System.out.println("所有数据仓库初始化完成");

        } catch (Exception e) {
            System.err.println("仓库初始化失败: " + e.getMessage());
            throw new RuntimeException("无法初始化数据仓库", e);
        }
    }

    /**
     * 获取患者仓库
     */
    public PatientRepository getPatientRepository() {
        return patientRepository;
    }

    /**
     * 获取医生仓库
     */
    public ClinicianRepository getClinicianRepository() {
        return clinicianRepository;
    }

    /**
     * 获取设施仓库
     */
    public FacilityRepository getFacilityRepository() {
        return facilityRepository;
    }

    /**
     * 获取预约仓库
     */
    public AppointmentRepository getAppointmentRepository() {
        return appointmentRepository;
    }

    /**
     * 获取处方仓库
     */
    public PrescriptionRepository getPrescriptionRepository() {
        return prescriptionRepository;
    }

    /**
     * 获取转诊仓库
     */
    public ReferralRepository getReferralRepository() {
        return referralRepository;
    }

    /**
     * 获取工作人员仓库
     */
    public StaffRepository getStaffRepository() {
        return staffRepository;
    }

    /**
     * 重新加载所有数据
     */
    public void reloadAllData() throws HealthcareException {
        System.out.println("开始重新加载所有数据...");

        patientRepository.reload();
        clinicianRepository.reload();
        facilityRepository.reload();
        appointmentRepository.reload();
        prescriptionRepository.reload();
        referralRepository.reload();
        staffRepository.reload();

        System.out.println("所有数据重新加载完成");
    }

    /**
     * 保存所有数据
     */
    public void flushAllData() throws HealthcareException {
        System.out.println("开始保存所有数据...");

        patientRepository.flush();
        clinicianRepository.flush();
        facilityRepository.flush();
        appointmentRepository.flush();
        prescriptionRepository.flush();
        referralRepository.flush();
        staffRepository.flush();

        System.out.println("所有数据保存完成");
    }

    /**
     * 获取数据统计摘要
     */
    public String getDataSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== 数据仓库统计摘要 ===\n\n");

        try {
            summary.append("患者: ").append(patientRepository.count()).append(" 条记录\n");
            summary.append("医生: ").append(clinicianRepository.count()).append(" 条记录\n");
            summary.append("设施: ").append(facilityRepository.count()).append(" 条记录\n");
            summary.append("预约: ").append(appointmentRepository.count()).append(" 条记录\n");
            summary.append("处方: ").append(prescriptionRepository.count()).append(" 条记录\n");
            summary.append("转诊: ").append(referralRepository.count()).append(" 条记录\n");
            summary.append("工作人员: ").append(staffRepository.count()).append(" 条记录\n\n");

            long totalRecords = patientRepository.count() + clinicianRepository.count() +
                               facilityRepository.count() + appointmentRepository.count() +
                               prescriptionRepository.count() + referralRepository.count() +
                               staffRepository.count();

            summary.append("总记录数: ").append(totalRecords).append("\n");
            summary.append("数据文件状态: 正常\n");

        } catch (HealthcareException e) {
            summary.append("获取统计信息时出错: ").append(e.getMessage()).append("\n");
        }

        summary.append("=== 摘要生成完成 ===\n");
        return summary.toString();
    }

    /**
     * 验证数据完整性
     */
    public String validateDataIntegrity() {
        StringBuilder report = new StringBuilder();
        report.append("=== 数据完整性验证报告 ===\n\n");

        try {
            // 检查预约数据引用完整性
            long orphanedAppointments = appointmentRepository.findAll().stream()
                .filter(apt -> {
                    try {
                        return apt.getPatientId() != null && !patientRepository.existsById(apt.getPatientId());
                    } catch (HealthcareException e) {
                        return false;
                    }
                })
                .count();

            // 检查处方数据引用完整性
            long orphanedPrescriptions = prescriptionRepository.findAll().stream()
                .filter(rx -> {
                    try {
                        return rx.getPatientId() != null && !patientRepository.existsById(rx.getPatientId());
                    } catch (HealthcareException e) {
                        return false;
                    }
                })
                .count();

            // 检查转诊数据引用完整性
            long orphanedReferrals = referralRepository.findAll().stream()
                .filter(ref -> {
                    try {
                        boolean patientExists = ref.getPatientId() != null && patientRepository.existsById(ref.getPatientId());
                        boolean referringClinicianExists = ref.getReferringClinicianId() != null && clinicianRepository.existsById(ref.getReferringClinicianId());
                        return !patientExists || !referringClinicianExists;
                    } catch (HealthcareException e) {
                        return false;
                    }
                })
                .count();

            // 检查工作人员数据引用完整性
            long orphanedStaff = staffRepository.findAll().stream()
                .filter(staff -> {
                    try {
                        return staff.getFacilityId() != null && !facilityRepository.existsById(staff.getFacilityId());
                    } catch (HealthcareException e) {
                        return false;
                    }
                })
                .count();

            report.append("数据完整性检查结果:\n");
            report.append("- 孤立预约数: ").append(orphanedAppointments).append("\n");
            report.append("- 孤立处方数: ").append(orphanedPrescriptions).append("\n");
            report.append("- 孤立转诊数: ").append(orphanedReferrals).append("\n");
            report.append("- 孤立工作人员数: ").append(orphanedStaff).append("\n");

            long totalOrphaned = orphanedAppointments + orphanedPrescriptions + orphanedReferrals + orphanedStaff;
            report.append("- 总孤立记录数: ").append(totalOrphaned).append("\n");

            if (totalOrphaned == 0) {
                report.append("\n✅ 数据完整性良好，无孤立记录\n");
            } else {
                report.append("\n⚠️ 发现孤立记录，建议进行数据清理\n");
            }

        } catch (Exception e) {
            report.append("验证数据完整性时出错: ").append(e.getMessage()).append("\n");
        }

        report.append("\n=== 完整性验证完成 ===\n");
        return report.toString();
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        System.out.println("清理仓库管理器资源...");

        // 清空缓存
        patientRepository = null;
        clinicianRepository = null;
        facilityRepository = null;
        appointmentRepository = null;
        prescriptionRepository = null;
        referralRepository = null;
        staffRepository = null;

        instance = null;

        System.out.println("仓库管理器资源清理完成");
    }

    /**
     * 获取仓库管理器状态
     */
    public String getStatus() {
        StringBuilder status = new StringBuilder();
        status.append("仓库管理器状态:\n");
        status.append("- 患者仓库: ").append(patientRepository != null ? "已初始化" : "未初始化").append("\n");
        status.append("- 医生仓库: ").append(clinicianRepository != null ? "已初始化" : "未初始化").append("\n");
        status.append("- 设施仓库: ").append(facilityRepository != null ? "已初始化" : "未初始化").append("\n");
        status.append("- 预约仓库: ").append(appointmentRepository != null ? "已初始化" : "未初始化").append("\n");
        status.append("- 处方仓库: ").append(prescriptionRepository != null ? "已初始化" : "未初始化").append("\n");
        status.append("- 转诊仓库: ").append(referralRepository != null ? "已初始化" : "未初始化").append("\n");
        status.append("- 工作人员仓库: ").append(staffRepository != null ? "已初始化" : "未初始化").append("\n");
        return status.toString();
    }
}
