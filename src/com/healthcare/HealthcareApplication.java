package com.healthcare;

/**
 * 医疗保健管理系统应用主类
 * 负责初始化MVC组件和启动系统
 *
 * @author Healthcare System
 * @version 1.0
 */
public class HealthcareApplication {

    // MVC组件
    private HealthcareController controller;
    private HealthcareView view;

    /**
     * 启动应用程序
     */
    public void start() {
        System.out.println("初始化MVC组件...");

        // 初始化Controller
        controller = new HealthcareController();

        // 初始化View
        view = new HealthcareView(controller);

        // 设置Controller的View引用
        controller.setView(view);

        System.out.println("MVC组件初始化完成");
    }

    /**
     * 获取控制器
     */
    public HealthcareController getController() {
        return controller;
    }

    /**
     * 获取视图
     */
    public HealthcareView getView() {
        return view;
    }
}
