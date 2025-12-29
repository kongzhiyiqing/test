package com.healthcare.util;

/**
 * 医疗保健系统异常类
 * 统一的异常处理
 *
 * @author Healthcare System
 * @version 1.0
 */
public class HealthcareException extends Exception {

    /**
     * 构造函数
     */
    public HealthcareException(String message) {
        super(message);
    }

    /**
     * 构造函数
     */
    public HealthcareException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 静态工厂方法 - 数据加载异常
     */
    public static HealthcareException dataLoadError(String dataType, Throwable cause) {
        return new HealthcareException("加载" + dataType + "数据失败: " + cause.getMessage(), cause);
    }

    /**
     * 静态工厂方法 - 数据保存异常
     */
    public static HealthcareException dataSaveError(String dataType, Throwable cause) {
        return new HealthcareException("保存" + dataType + "数据失败: " + cause.getMessage(), cause);
    }

    /**
     * 静态工厂方法 - 数据验证异常
     */
    public static HealthcareException validationError(String field, String reason) {
        return new HealthcareException("字段 '" + field + "' 验证失败: " + reason);
    }

    /**
     * 静态工厂方法 - 业务逻辑异常
     */
    public static HealthcareException businessLogicError(String operation, String reason) {
        return new HealthcareException("业务操作 '" + operation + "' 失败: " + reason);
    }
}
