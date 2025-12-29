package com.healthcare.util;

import com.healthcare.repository.RepositoryManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 性能监控工具
 * 监控数据仓库的性能指标
 *
 * @author Healthcare System
 * @version 1.0
 */
public class PerformanceMonitor {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 性能指标
    private Map<String, OperationMetrics> metrics;

    // 单例模式
    private static PerformanceMonitor instance;

    /**
     * 私有构造函数
     */
    private PerformanceMonitor() {
        this.metrics = new HashMap<>();
        initializeMetrics();
    }

    /**
     * 获取单例实例
     */
    public static synchronized PerformanceMonitor getInstance() {
        if (instance == null) {
            instance = new PerformanceMonitor();
        }
        return instance;
    }

    /**
     * 初始化性能指标
     */
    private void initializeMetrics() {
        metrics.put("PatientRepository", new OperationMetrics("PatientRepository"));
        metrics.put("ClinicianRepository", new OperationMetrics("ClinicianRepository"));
        metrics.put("FacilityRepository", new OperationMetrics("FacilityRepository"));
        metrics.put("AppointmentRepository", new OperationMetrics("AppointmentRepository"));
        metrics.put("PrescriptionRepository", new OperationMetrics("PrescriptionRepository"));
        metrics.put("ReferralRepository", new OperationMetrics("ReferralRepository"));
        metrics.put("StaffRepository", new OperationMetrics("StaffRepository"));
    }

    /**
     * 记录操作开始
     */
    public void startOperation(String repositoryName, String operationName) {
        OperationMetrics repoMetrics = metrics.get(repositoryName);
        if (repoMetrics != null) {
            repoMetrics.startOperation(operationName);
        }
    }

    /**
     * 记录操作结束
     */
    public void endOperation(String repositoryName, String operationName) {
        OperationMetrics repoMetrics = metrics.get(repositoryName);
        if (repoMetrics != null) {
            repoMetrics.endOperation(operationName);
        }
    }

    /**
     * 记录操作执行时间
     */
    public void recordOperationTime(String repositoryName, String operationName, long durationMs) {
        OperationMetrics repoMetrics = metrics.get(repositoryName);
        if (repoMetrics != null) {
            repoMetrics.recordOperationTime(operationName, durationMs);
        }
    }

    /**
     * 获取性能报告
     */
    public String getPerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== 性能监控报告 ===\n");
        report.append("生成时间: ").append(LocalDateTime.now().format(TIME_FORMAT)).append("\n\n");

        for (OperationMetrics repoMetrics : metrics.values()) {
            report.append(repoMetrics.getReport()).append("\n");
        }

        report.append("=== 报告结束 ===\n");
        return report.toString();
    }

    /**
     * 获取总体性能统计
     */
    public String getOverallStatistics() {
        long totalOperations = metrics.values().stream()
                .mapToLong(OperationMetrics::getTotalOperations)
                .sum();

        double avgResponseTime = metrics.values().stream()
                .mapToDouble(OperationMetrics::getAverageResponseTime)
                .filter(time -> !Double.isNaN(time))
                .average()
                .orElse(0.0);

        long maxResponseTime = metrics.values().stream()
                .mapToLong(OperationMetrics::getMaxResponseTime)
                .max()
                .orElse(0L);

        return String.format("总体性能统计:\n总操作数: %d\n平均响应时间: %.2f ms\n最大响应时间: %d ms",
                totalOperations, avgResponseTime, maxResponseTime);
    }

    /**
     * 重置所有性能指标
     */
    public void resetAllMetrics() {
        for (OperationMetrics repoMetrics : metrics.values()) {
            repoMetrics.reset();
        }
        System.out.println("所有性能指标已重置");
    }

    /**
     * 保存性能报告到文件
     */
    public void saveReportToFile(String filePath) throws HealthcareException {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filePath))) {
            writer.print(getPerformanceReport());
            writer.print("\n");
            writer.print(getOverallStatistics());
            System.out.println("性能报告已保存到: " + filePath);
        } catch (java.io.IOException e) {
            throw HealthcareException.dataSaveError("性能报告文件", e);
        }
    }

    /**
     * 操作指标内部类
     */
    private static class OperationMetrics {
        private String repositoryName;
        private Map<String, OperationStats> operationStats;
        private long totalOperations;
        private long totalResponseTime;
        private long maxResponseTime;

        public OperationMetrics(String repositoryName) {
            this.repositoryName = repositoryName;
            this.operationStats = new HashMap<>();
            this.totalOperations = 0;
            this.totalResponseTime = 0;
            this.maxResponseTime = 0;
        }

        public void startOperation(String operationName) {
            OperationStats stats = operationStats.computeIfAbsent(operationName, k -> new OperationStats());
            stats.startTime = System.nanoTime();
        }

        public void endOperation(String operationName) {
            OperationStats stats = operationStats.get(operationName);
            if (stats != null && stats.startTime > 0) {
                long duration = (System.nanoTime() - stats.startTime) / 1_000_000; // 转换为毫秒
                stats.recordTime(duration);
                totalOperations++;
                totalResponseTime += duration;
                maxResponseTime = Math.max(maxResponseTime, duration);
            }
        }

        public void recordOperationTime(String operationName, long durationMs) {
            OperationStats stats = operationStats.computeIfAbsent(operationName, k -> new OperationStats());
            stats.recordTime(durationMs);
            totalOperations++;
            totalResponseTime += durationMs;
            maxResponseTime = Math.max(maxResponseTime, durationMs);
        }

        public long getTotalOperations() {
            return totalOperations;
        }

        public double getAverageResponseTime() {
            return totalOperations > 0 ? (double) totalResponseTime / totalOperations : 0.0;
        }

        public long getMaxResponseTime() {
            return maxResponseTime;
        }

        public String getReport() {
            StringBuilder report = new StringBuilder();
            report.append(repositoryName).append(" 性能指标:\n");
            report.append("- 总操作数: ").append(totalOperations).append("\n");
            report.append("- 平均响应时间: ").append(String.format("%.2f", getAverageResponseTime())).append(" ms\n");
            report.append("- 最大响应时间: ").append(maxResponseTime).append(" ms\n");

            if (!operationStats.isEmpty()) {
                report.append("- 操作详情:\n");
                for (Map.Entry<String, OperationStats> entry : operationStats.entrySet()) {
                    OperationStats stats = entry.getValue();
                    report.append("  * ").append(entry.getKey())
                          .append(": ").append(stats.getCount())
                          .append(" 次, 平均 ").append(String.format("%.2f", stats.getAverageTime()))
                          .append(" ms\n");
                }
            }

            return report.toString();
        }

        public void reset() {
            operationStats.clear();
            totalOperations = 0;
            totalResponseTime = 0;
            maxResponseTime = 0;
        }
    }

    /**
     * 操作统计内部类
     */
    private static class OperationStats {
        private long startTime;
        private long count;
        private long totalTime;
        private long maxTime;

        public void recordTime(long timeMs) {
            count++;
            totalTime += timeMs;
            maxTime = Math.max(maxTime, timeMs);
        }

        public long getCount() {
            return count;
        }

        public double getAverageTime() {
            return count > 0 ? (double) totalTime / count : 0.0;
        }

        public long getMaxTime() {
            return maxTime;
        }
    }
}
