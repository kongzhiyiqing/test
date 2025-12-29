package com.healthcare.controller;

/**
 * 医疗保健管理系统控制器
 * 处理业务逻辑和用户交互
 *
 * @author Healthcare System
 * @version 1.0
 */
public class HealthcareController extends BaseController {

    private PatientController patientController;
    private AppointmentController appointmentController;
    private PrescriptionController prescriptionController;
    private ReferralController referralController;

    /**
     * 构造函数
     */
    public HealthcareController() {
        initializeControllers();
    }

    /**
     * 初始化所有子控制器
     */
    private void initializeControllers() {
        patientController = new PatientController();
        appointmentController = new AppointmentController();
        prescriptionController = new PrescriptionController();
        referralController = new ReferralController();

        // 设置视图引用
        patientController.setView(view);
        appointmentController.setView(view);
        prescriptionController.setView(view);
        referralController.setView(view);

        logOperation("initializeControllers", "所有控制器初始化完成");
    }

    /**
     * 获取患者控制器
     */
    public PatientController getPatientController() {
        return patientController;
    }

    /**
     * 获取预约控制器
     */
    public AppointmentController getAppointmentController() {
        return appointmentController;
    }

    /**
     * 获取处方控制器
     */
    public PrescriptionController getPrescriptionController() {
        return prescriptionController;
    }

    /**
     * 获取转诊控制器
     */
    public ReferralController getReferralController() {
        return referralController;
    }

    /**
     * 启动系统
     */
    public void startSystem() {
        logOperation("startSystem", "医疗保健管理系统控制器启动");
        if (view != null) {
            view.showMainMenu();
        }
    }

    /**
     * 处理患者管理
     */
    public void handlePatientManagement() {
        logOperation("handlePatientManagement", "处理患者管理请求");
        if (view instanceof com.healthcare.view.HealthcareView) {
            ((com.healthcare.view.HealthcareView) view).showPatientManagement();
        }
        showMessage("患者管理模块 - 共 " + patientController.getPatientCount() + " 位患者");
        showMessage("年龄统计:\n" + patientController.getPatientAgeStatistics());
    }

    /**
     * 处理预约管理
     */
    public void handleAppointmentManagement() {
        logOperation("handleAppointmentManagement", "处理预约管理请求");
        if (view instanceof com.healthcare.view.HealthcareView) {
            ((com.healthcare.view.HealthcareView) view).showAppointmentManagement();
        }
        showMessage("预约管理模块 - 共 " + appointmentController.getAppointmentCount() + " 个预约");
        showMessage("预约统计:\n" + appointmentController.getAppointmentStatistics());
    }

    /**
     * 处理处方管理
     */
    public void handlePrescriptionManagement() {
        logOperation("handlePrescriptionManagement", "处理处方管理请求");
        if (view instanceof com.healthcare.view.HealthcareView) {
            ((com.healthcare.view.HealthcareView) view).showPrescriptionManagement();
        }
        showMessage("处方管理模块 - 共 " + prescriptionController.getPrescriptionCount() + " 个处方");
        showMessage("处方统计:\n" + prescriptionController.getPrescriptionStatistics());
    }

    /**
     * 处理转诊管理
     */
    public void handleReferralManagement() {
        logOperation("handleReferralManagement", "处理转诊管理请求");
        if (view instanceof com.healthcare.view.HealthcareView) {
            ((com.healthcare.view.HealthcareView) view).showReferralManagement();
        }
        showMessage("转诊管理模块 - 共 " + referralController.getReferralCount() + " 个转诊");
        showMessage("转诊统计:\n" + referralController.getReferralStatistics());
    }

    /**
     * 退出系统
     */
    public void exitSystem() {
        logOperation("exitSystem", "正在退出系统");
        showMessage("感谢使用医疗保健管理系统！");

        // 清理资源
        patientController.cleanup();
        appointmentController.cleanup();
        prescriptionController.cleanup();
        referralController.cleanup();

        System.exit(0);
    }

    @Override
    public void initialize() {
        logOperation("initialize", "主控制器初始化完成");
    }

    @Override
    public void cleanup() {
        logOperation("cleanup", "主控制器清理完成");
    }
}
