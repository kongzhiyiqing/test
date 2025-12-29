package com.healthcare.service;

import com.healthcare.util.HealthcareException;

/**
 * 医疗保健服务接口
 * 定义医疗保健系统的核心业务服务
 *
 * @author Healthcare System
 * @version 1.0
 */
public interface HealthcareService {

    /**
     * 初始化服务
     */
    void initialize() throws HealthcareException;

    /**
     * 关闭服务
     */
    void shutdown() throws HealthcareException;

    /**
     * 获取服务状态
     */
    String getServiceStatus();

    /**
     * 验证服务健康状态
     */
    boolean isHealthy();

    /**
     * 获取服务统计信息
     */
    String getStatistics();
}
