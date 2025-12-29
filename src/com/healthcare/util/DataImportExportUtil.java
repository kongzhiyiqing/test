package com.healthcare.util;

import com.healthcare.model.*;
import com.healthcare.repository.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 数据导入导出工具类
 * 提供数据备份、恢复和迁移功能
 *
 * @author Healthcare System
 * @version 1.0
 */
public class DataImportExportUtil {

    private static final DateTimeFormatter BACKUP_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * 导出所有数据到备份文件
     */
    public static void exportAllData(String backupDirectory) throws HealthcareException {
        String timestamp = LocalDateTime.now().format(BACKUP_TIMESTAMP_FORMAT);
        String backupDir = backupDirectory + "/backup_" + timestamp;

        try {
            // 创建备份目录
            Path backupPath = Paths.get(backupDir);
            Files.createDirectories(backupPath);

            System.out.println("开始导出数据到: " + backupDir);

            // 导出各个数据文件
            exportDataFile("patients.csv", backupDir + "/patients.csv");
            exportDataFile("clinicians.csv", backupDir + "/clinicians.csv");
            exportDataFile("facilities.csv", backupDir + "/facilities.csv");
            exportDataFile("appointments.csv", backupDir + "/appointments.csv");
            exportDataFile("prescriptions.csv", backupDir + "/prescriptions.csv");
            exportDataFile("referrals.csv", backupDir + "/referrals.csv");
            exportDataFile("staff.csv", backupDir + "/staff.csv");

            // 创建备份清单
            createBackupManifest(backupDir, timestamp);

            System.out.println("数据导出完成: " + backupDir);

        } catch (IOException e) {
            throw HealthcareException.dataSaveError("备份文件", e);
        }
    }

    /**
     * 从备份文件导入所有数据
     */
    public static void importAllData(String backupDirectory) throws HealthcareException {
        Path backupPath = Paths.get(backupDirectory);
        if (!Files.exists(backupPath) || !Files.isDirectory(backupPath)) {
            throw new HealthcareException("备份目录不存在: " + backupDirectory);
        }

        System.out.println("开始从备份导入数据: " + backupDirectory);

        try {
            // 验证备份清单
            validateBackupManifest(backupDirectory);

            // 导入各个数据文件
            importDataFile(backupDirectory + "/patients.csv", "patients.csv");
            importDataFile(backupDirectory + "/clinicians.csv", "clinicians.csv");
            importDataFile(backupDirectory + "/facilities.csv", "facilities.csv");
            importDataFile(backupDirectory + "/appointments.csv", "appointments.csv");
            importDataFile(backupDirectory + "/prescriptions.csv", "prescriptions.csv");
            importDataFile(backupDirectory + "/referrals.csv", "referrals.csv");
            importDataFile(backupDirectory + "/staff.csv", "staff.csv");

            System.out.println("数据导入完成");

        } catch (IOException e) {
            throw HealthcareException.dataLoadError("备份文件", e);
        }
    }

    /**
     * 导出单个数据文件
     */
    private static void exportDataFile(String sourceFile, String targetFile) throws IOException {
        Path sourcePath = Paths.get(sourceFile);
        if (Files.exists(sourcePath)) {
            Files.copy(sourcePath, Paths.get(targetFile));
            System.out.println("已导出: " + sourceFile);
        } else {
            System.out.println("文件不存在，跳过: " + sourceFile);
        }
    }

    /**
     * 导入单个数据文件
     */
    private static void importDataFile(String sourceFile, String targetFile) throws IOException {
        Path sourcePath = Paths.get(sourceFile);
        if (Files.exists(sourcePath)) {
            Files.copy(sourcePath, Paths.get(targetFile));
            System.out.println("已导入: " + targetFile);
        } else {
            System.out.println("备份文件不存在，跳过: " + sourceFile);
        }
    }

    /**
     * 创建备份清单
     */
    private static void createBackupManifest(String backupDir, String timestamp) throws IOException {
        try (PrintWriter writer = new PrintWriter(backupDir + "/backup_manifest.txt")) {
            writer.println("医疗保健管理系统数据备份");
            writer.println("备份时间: " + timestamp);
            writer.println("备份日期: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println();
            writer.println("包含的文件:");
            writer.println("- patients.csv (患者数据)");
            writer.println("- clinicians.csv (医生数据)");
            writer.println("- facilities.csv (设施数据)");
            writer.println("- appointments.csv (预约数据)");
            writer.println("- prescriptions.csv (处方数据)");
            writer.println("- referrals.csv (转诊数据)");
            writer.println("- staff.csv (工作人员数据)");
            writer.println();
            writer.println("备份创建者: Healthcare System v1.0");
        }
    }

    /**
     * 验证备份清单
     */
    private static void validateBackupManifest(String backupDir) throws HealthcareException, IOException {
        Path manifestPath = Paths.get(backupDir + "/backup_manifest.txt");
        if (!Files.exists(manifestPath)) {
            System.out.println("警告: 备份清单文件不存在");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(manifestPath.toFile()))) {
            String line = reader.readLine();
            if (line == null || !line.contains("医疗保健管理系统数据备份")) {
                throw new HealthcareException("无效的备份清单文件");
            }
            System.out.println("备份清单验证通过");
        }
    }

    /**
     * 生成数据摘要报告
     */
    public static String generateDataSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== 医疗保健管理系统数据摘要 ===\n\n");

        try {
            // 患者统计
            PatientRepository patientRepo = new PatientRepository();
            summary.append("患者统计:\n");
            summary.append("- 总患者数: ").append(patientRepo.count()).append("\n");
            summary.append(patientRepo.getAgeStatistics()).append("\n\n");

            // 预约统计
            AppointmentRepository appointmentRepo = new AppointmentRepository();
            summary.append("预约统计:\n");
            summary.append("- 总预约数: ").append(appointmentRepo.count()).append("\n");
            summary.append(appointmentRepo.getAppointmentStatistics()).append("\n\n");

            // 处方统计
            PrescriptionRepository prescriptionRepo = new PrescriptionRepository();
            summary.append("处方统计:\n");
            summary.append("- 总处方数: ").append(prescriptionRepo.count()).append("\n");
            summary.append(prescriptionRepo.getPrescriptionStatistics()).append("\n\n");

            // 转诊统计
            ReferralRepository referralRepo = new ReferralRepository();
            summary.append("转诊统计:\n");
            summary.append("- 总转诊数: ").append(referralRepo.count()).append("\n");
            summary.append(referralRepo.getReferralStatistics()).append("\n\n");

            summary.append("=== 数据摘要生成完成 ===\n");

        } catch (Exception e) {
            summary.append("生成数据摘要时出错: ").append(e.getMessage()).append("\n");
        }

        return summary.toString();
    }

    /**
     * 将数据摘要保存到文件
     */
    public static void saveDataSummaryToFile(String filePath) throws HealthcareException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.print(generateDataSummary());
            System.out.println("数据摘要已保存到: " + filePath);
        } catch (IOException e) {
            throw HealthcareException.dataSaveError("数据摘要文件", e);
        }
    }

    /**
     * 清理临时文件
     */
    public static void cleanupTempFiles(String directory) throws HealthcareException {
        try {
            Path dirPath = Paths.get(directory);
            if (Files.exists(dirPath) && Files.isDirectory(dirPath)) {
                Files.walk(dirPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".tmp") ||
                                   path.toString().endsWith(".bak"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            System.out.println("已删除临时文件: " + path);
                        } catch (IOException e) {
                            System.err.println("删除临时文件失败: " + path + " - " + e.getMessage());
                        }
                    });
            }
        } catch (IOException e) {
            throw HealthcareException.businessLogicError("cleanupTempFiles", "清理临时文件失败: " + e.getMessage());
        }
    }

    /**
     * 验证数据完整性
     */
    public static String validateDataIntegrity() {
        StringBuilder report = new StringBuilder();
        report.append("=== 数据完整性验证报告 ===\n\n");

        try {
            // 检查患者数据
            PatientRepository patientRepo = new PatientRepository();
            List<Patient> patients = patientRepo.findAll();
            report.append("患者数据检查:\n");
            report.append("- 总记录数: ").append(patients.size()).append("\n");

            long invalidPatients = patients.stream().filter(p -> !p.isValid()).count();
            report.append("- 无效记录数: ").append(invalidPatients).append("\n");

            // 检查预约数据引用完整性
            AppointmentRepository appointmentRepo = new AppointmentRepository();
            List<Appointment> appointments = appointmentRepo.findAll();
            report.append("\n预约数据检查:\n");
            report.append("- 总记录数: ").append(appointments.size()).append("\n");

            long orphanedAppointments = appointments.stream()
                .filter(a -> a.getPatientId() != null && patientRepo.findById(a.getPatientId()).isEmpty())
                .count();
            report.append("- 孤立预约数: ").append(orphanedAppointments).append("\n");

            // 检查处方数据引用完整性
            PrescriptionRepository prescriptionRepo = new PrescriptionRepository();
            List<Prescription> prescriptions = prescriptionRepo.findAll();
            report.append("\n处方数据检查:\n");
            report.append("- 总记录数: ").append(prescriptions.size()).append("\n");

            long orphanedPrescriptions = prescriptions.stream()
                .filter(p -> p.getPatientId() != null && patientRepo.findById(p.getPatientId()).isEmpty())
                .count();
            report.append("- 孤立处方数: ").append(orphanedPrescriptions).append("\n");

            report.append("\n=== 数据完整性验证完成 ===\n");

        } catch (Exception e) {
            report.append("验证数据完整性时出错: ").append(e.getMessage()).append("\n");
        }

        return report.toString();
    }
}
