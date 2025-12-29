package com.healthcare.service;

import com.healthcare.util.HealthcareException;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务管理器
 * 统一管理所有业务服务实例
 *
 * @author Healthcare System
 * @version 1.0
 */
public class ServiceManager {

    // 服务实例
    private PatientService patientService;
    private AppointmentService appointmentService;
    private PrescriptionService prescriptionService;
    private ReferralService referralService;

    // 单例模式
    private static ServiceManager instance;

    /**
     * 私有构造函数
     */
    private ServiceManager() {
        initializeServices();
    }

    /**
     * 获取单例实例
     */
    public static synchronized ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

    /**
     * 初始化所有服务
     */
    private void initializeServices() {
        try {
            patientService = new PatientService();
            appointmentService = new AppointmentService();
            prescriptionService = new PrescriptionService();
            referralService = new ReferralService();

            System.out.println("所有业务服务初始化完成");

        } catch (Exception e) {
            System.err.println("服务初始化失败: " + e.getMessage());
            throw new RuntimeException("无法初始化业务服务", e);
        }
    }

    /**
     * 初始化所有服务
     */
    public void initializeAllServices() throws HealthcareException {
        System.out.println("正在初始化所有业务服务...");

        patientService.initialize();
        appointmentService.initialize();
        prescriptionService.initialize();
        referralService.initialize();

        System.out.println("所有业务服务初始化完成");
    }

    /**
     * 关闭所有服务
     */
    public void shutdownAllServices() throws HealthcareException {
        System.out.println("正在关闭所有业务服务...");

        if (referralService != null) referralService.shutdown();
        if (prescriptionService != null) prescriptionService.shutdown();
        if (appointmentService != null) appointmentService.shutdown();
        if (patientService != null) patientService.shutdown();

        System.out.println("所有业务服务已关闭");
    }

    /**
     * 获取患者服务
     */
    public PatientService getPatientService() {
        return patientService;
    }

    /**
     * 获取预约服务
     */
    public AppointmentService getAppointmentService() {
        return appointmentService;
    }

    /**
     * 获取处方服务
     */
    public PrescriptionService getPrescriptionService() {
        return prescriptionService;
    }

    /**
     * 获取转诊服务
     */
    public ReferralService getReferralService() {
        return referralService;
    }

    /**
     * 获取服务健康状态
     */
    public boolean areAllServicesHealthy() {
        return patientService.isHealthy() &&
               appointmentService.isHealthy() &&
               prescriptionService.isHealthy() &&
               referralService.isHealthy();
    }

    /**
     * 获取服务状态摘要
     */
    public String getServicesStatus() {
        StringBuilder status = new StringBuilder();
        status.append("=== 业务服务状态 ===\n");

        status.append("患者服务: ").append(getServiceStatus(patientService)).append("\n");
        status.append("预约服务: ").append(getServiceStatus(appointmentService)).append("\n");
        status.append("处方服务: ").append(getServiceStatus(prescriptionService)).append("\n");
        status.append("转诊服务: ").append(getServiceStatus(referralService)).append("\n");

        status.append("\n整体状态: ").append(areAllServicesHealthy() ? "健康" : "异常").append("\n");

        return status.toString();
    }

    /**
     * 获取服务统计信息
     */
    public String getServicesStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== 业务服务统计 ===\n\n");

        try {
            stats.append("患者服务:\n").append(patientService.getStatistics()).append("\n\n");
            stats.append("预约服务:\n").append(appointmentService.getStatistics()).append("\n\n");
            stats.append("处方服务:\n").append(prescriptionService.getStatistics()).append("\n\n");
            stats.append("转诊服务:\n").append(referralService.getStatistics()).append("\n\n");

        } catch (Exception e) {
            stats.append("获取统计信息时出错: ").append(e.getMessage()).append("\n");
        }

        return stats.toString();
    }

    /**
     * 执行系统健康检查
     */
    public HealthCheckResult performHealthCheck() {
        HealthCheckResult result = new HealthCheckResult();

        try {
            // 检查各个服务
            checkServiceHealth(patientService, "PatientService", result);
            checkServiceHealth(appointmentService, "AppointmentService", result);
            checkServiceHealth(prescriptionService, "PrescriptionService", result);
            checkServiceHealth(referralService, "ReferralService", result);

            result.setOverallHealthy(result.getFailedServices().isEmpty());

        } catch (Exception e) {
            result.setOverallHealthy(false);
            result.addFailedService("HealthCheck", "健康检查过程失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 检查单个服务健康状态
     */
    private void checkServiceHealth(HealthcareService service, String serviceName, HealthCheckResult result) {
        try {
            boolean healthy = service.isHealthy();
            if (healthy) {
                result.addHealthyService(serviceName);
            } else {
                result.addFailedService(serviceName, "服务报告不健康状态");
            }
        } catch (Exception e) {
            result.addFailedService(serviceName, "健康检查异常: " + e.getMessage());
        }
    }

    /**
     * 获取服务状态字符串
     */
    private String getServiceStatus(HealthcareService service) {
        try {
            return service.isHealthy() ? "正常" : "异常";
        } catch (Exception e) {
            return "检查失败";
        }
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        System.out.println("清理服务管理器资源...");

        patientService = null;
        appointmentService = null;
        prescriptionService = null;
        referralService = null;

        instance = null;

        System.out.println("服务管理器资源清理完成");
    }

    /**
     * 健康检查结果类
     */
    public static class HealthCheckResult {
        private boolean overallHealthy;
        private Map<String, String> healthyServices;
        private Map<String, String> failedServices;

        public HealthCheckResult() {
            this.healthyServices = new HashMap<>();
            this.failedServices = new HashMap<>();
        }

        public void setOverallHealthy(boolean overallHealthy) {
            this.overallHealthy = overallHealthy;
        }

        public boolean isOverallHealthy() {
            return overallHealthy;
        }

        public void addHealthyService(String serviceName) {
            healthyServices.put(serviceName, "正常");
        }

        public void addFailedService(String serviceName, String reason) {
            failedServices.put(serviceName, reason);
        }

        public Map<String, String> getHealthyServices() {
            return healthyServices;
        }

        public Map<String, String> getFailedServices() {
            return failedServices;
        }

        public String getReport() {
            StringBuilder report = new StringBuilder();
            report.append("健康检查结果: ").append(overallHealthy ? "通过" : "失败").append("\n\n");

            if (!healthyServices.isEmpty()) {
                report.append("正常服务 (").append(healthyServices.size()).append("个):\n");
                healthyServices.forEach((name, status) ->
                    report.append("- ").append(name).append(": ").append(status).append("\n"));
            }

            if (!failedServices.isEmpty()) {
                report.append("\n异常服务 (").append(failedServices.size()).append("个):\n");
                failedServices.forEach((name, reason) ->
                    report.append("- ").append(name).append(": ").append(reason).append("\n"));
            }

            return report.toString();
        }
    }
}
