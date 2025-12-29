package com.healthcare.controller;

import com.healthcare.util.HealthcareException;
import com.healthcare.view.HealthcareView;

/**
 * 基础控制器类
 * 所有控制器的基类，提供通用功能
 *
 * @author Healthcare System
 * @version 1.0
 */
public abstract class BaseController {

    protected HealthcareView view;

    /**
     * 设置视图引用
     */
    public void setView(HealthcareView view) {
        this.view = view;
    }

    /**
     * 获取视图引用
     */
    public HealthcareView getView() {
        return view;
    }

    /**
     * 显示信息消息
     */
    protected void showMessage(String message) {
        if (view != null) {
            view.showMessage(message);
        } else {
            System.out.println("信息: " + message);
        }
    }

    /**
     * 显示错误消息
     */
    protected void showError(String message) {
        if (view != null) {
            view.showError(message);
        } else {
            System.err.println("错误: " + message);
        }
    }

    /**
     * 处理异常
     */
    protected void handleException(Exception e) {
        String errorMessage = "操作失败: " + e.getMessage();
        showError(errorMessage);
        e.printStackTrace();
    }

    /**
     * 验证输入参数
     */
    protected void validateNotNull(Object obj, String paramName) throws HealthcareException {
        if (obj == null) {
            throw HealthcareException.validationError(paramName, "不能为空");
        }
    }

    /**
     * 验证字符串不为空
     */
    protected void validateNotEmpty(String str, String paramName) throws HealthcareException {
        if (str == null || str.trim().isEmpty()) {
            throw HealthcareException.validationError(paramName, "不能为空");
        }
    }

    /**
     * 验证数字范围
     */
    protected void validateRange(int value, int min, int max, String paramName) throws HealthcareException {
        if (value < min || value > max) {
            throw HealthcareException.validationError(paramName, "必须在" + min + "到" + max + "之间");
        }
    }

    /**
     * 记录操作日志
     */
    protected void logOperation(String operation, String details) {
        System.out.println("[LOG] " + operation + ": " + details);
    }

    /**
     * 初始化控制器
     */
    public abstract void initialize();

    /**
     * 清理资源
     */
    public abstract void cleanup();
}
