package com.healthcare;

/**
 * 医疗保健管理系统主启动类
 * 基于MVC架构设计
 *
 * @author Healthcare System
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== 医疗保健管理系统启动中 ===");

        try {
            // 初始化系统
            HealthcareApplication application = new HealthcareApplication();
            application.start();

            // 启动控制器
            application.getController().startSystem();

        } catch (Exception e) {
            System.err.println("系统启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
