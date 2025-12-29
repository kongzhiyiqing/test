package com.healthcare.service;

import com.healthcare.repository.RepositoryManager;
import com.healthcare.util.HealthcareException;
import com.healthcare.util.PerformanceMonitor;

/**
 * 基础服务抽象类
 * 提供通用服务功能
 *
 * @author Healthcare System
 * @version 1.0
 */
public abstract class BaseService implements HealthcareService {

    protected RepositoryManager repositoryManager;
    protected PerformanceMonitor performanceMonitor;
    protected boolean initialized;

    /**
     * 构造函数
     */
    protected BaseService() {
        this.repositoryManager = RepositoryManager.getInstance();
        this.performanceMonitor = PerformanceMonitor.getInstance();
        this.initialized = false;
    }

    /**
     * 验证服务是否已初始化
     */
    protected void validateInitialized() throws HealthcareException {
        if (!initialized) {
            throw new HealthcareException(getClass().getSimpleName() + " 服务尚未初始化");
        }
    }

    /**
     * 记录操作性能
     */
    protected void recordOperation(String operationName, Runnable operation) throws HealthcareException {
        String serviceName = getClass().getSimpleName();
        performanceMonitor.startOperation(serviceName, operationName);

        try {
            operation.run();
        } finally {
            performanceMonitor.endOperation(serviceName, operationName);
        }
    }

    /**
     * 执行操作并返回结果
     */
    protected <T> T executeOperation(String operationName, java.util.function.Supplier<T> operation) throws HealthcareException {
        String serviceName = getClass().getSimpleName();
        performanceMonitor.startOperation(serviceName, operationName);

        try {
            return operation.get();
        } finally {
            performanceMonitor.endOperation(serviceName, operationName);
        }
    }

    /**
     * 验证业务规则
     */
    protected void validateBusinessRule(boolean condition, String errorMessage) throws HealthcareException {
        if (!condition) {
            throw new HealthcareException("业务规则验证失败: " + errorMessage);
        }
    }

    /**
     * 验证参数
     */
    protected void validateParameter(Object parameter, String parameterName) throws HealthcareException {
        if (parameter == null) {
            throw new HealthcareException("参数验证失败: " + parameterName + " 不能为空");
        }
    }

    /**
     * 验证字符串参数
     */
    protected void validateStringParameter(String parameter, String parameterName) throws HealthcareException {
        if (parameter == null || parameter.trim().isEmpty()) {
            throw new HealthcareException("参数验证失败: " + parameterName + " 不能为空字符串");
        }
    }

    /**
     * 记录服务事件
     */
    protected void logServiceEvent(String event) {
        System.out.println("[" + getClass().getSimpleName() + "] " + event);
    }

    /**
     * 获取服务基本状态
     */
    protected String getBasicStatus() {
        return getClass().getSimpleName() + " 状态: " +
               (initialized ? "已初始化" : "未初始化") + ", " +
               (isHealthy() ? "健康" : "异常");
    }

    @Override
    public boolean isHealthy() {
        return initialized && repositoryManager != null;
    }

    @Override
    public void shutdown() throws HealthcareException {
        logServiceEvent("正在关闭服务...");
        initialized = false;
        logServiceEvent("服务已关闭");
    }
}
