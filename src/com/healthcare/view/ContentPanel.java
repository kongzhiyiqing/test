package com.healthcare.view;

import com.healthcare.controller.HealthcareController;

import javax.swing.*;
import java.awt.*;

/**
 * 内容面板
 * 显示当前选中的功能模块内容
 *
 * @author Healthcare System
 * @version 1.0
 */
public class ContentPanel extends JPanel {

    private HealthcareController controller;
    private HealthcareView parentView;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // 内容面板
    private JPanel homePanel;
    private BasePanel patientPanel;
    private BasePanel appointmentPanel;
    private BasePanel prescriptionPanel;
    private BasePanel referralPanel;

    /**
     * 构造函数
     */
    public ContentPanel(HealthcareController controller, HealthcareView parentView) {
        this.controller = controller;
        this.parentView = parentView;
        initializeComponents();
        setupLayout();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 创建各个功能面板
        createHomePanel();
        createPatientPanel();
        createAppointmentPanel();
        createPrescriptionPanel();
        createReferralPanel();

        // 添加到卡片布局
        cardPanel.add(homePanel, "HOME");
        cardPanel.add(patientPanel, "PATIENT");
        cardPanel.add(appointmentPanel, "APPOINTMENT");
        cardPanel.add(prescriptionPanel, "PRESCRIPTION");
        cardPanel.add(referralPanel, "REFERRAL");
    }

    /**
     * 创建主页面板
     */
    private void createHomePanel() {
        homePanel = GuiUtils.createPanel(new BorderLayout());

        // 标题
        JPanel titlePanel = GuiUtils.createPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.add(GuiUtils.createTitleLabel("🏥 欢迎使用医疗保健管理系统"));
        homePanel.add(titlePanel, BorderLayout.NORTH);

        // 主要内容
        JPanel contentPanel = GuiUtils.createPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // 系统概述
        JTextArea overviewArea = GuiUtils.createTextArea(10, 50);
        overviewArea.setEditable(false);
        overviewArea.setText(getSystemOverview());
        overviewArea.setBackground(GuiUtils.BACKGROUND_COLOR);

        JScrollPane scrollPane = GuiUtils.createScrollPane(overviewArea);

        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(scrollPane, gbc);

        homePanel.add(contentPanel, BorderLayout.CENTER);

        // 底部信息
        JPanel footerPanel = GuiUtils.createPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.add(GuiUtils.createLabel("选择左侧导航栏进入相应功能模块"));
        homePanel.add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建患者管理面板
     */
    private void createPatientPanel() {
        patientPanel = new PatientPanel(controller, parentView);
    }

    /**
     * 创建预约管理面板
     */
    private void createAppointmentPanel() {
        appointmentPanel = new AppointmentPanel(controller, parentView);
    }

    /**
     * 创建处方管理面板
     */
    private void createPrescriptionPanel() {
        prescriptionPanel = new PrescriptionPanel(controller, parentView);
    }

    /**
     * 创建转诊管理面板
     */
    private void createReferralPanel() {
        referralPanel = new ReferralPanel(controller, parentView);
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);
    }

    /**
     * 显示指定面板
     */
    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);

        // 刷新对应面板的数据
        switch (panelName) {
            case "PATIENT":
                if (patientPanel != null) patientPanel.refreshData();
                break;
            case "APPOINTMENT":
                if (appointmentPanel != null) appointmentPanel.refreshData();
                break;
            case "PRESCRIPTION":
                if (prescriptionPanel != null) prescriptionPanel.refreshData();
                break;
            case "REFERRAL":
                if (referralPanel != null) referralPanel.refreshData();
                break;
        }
    }

    /**
     * 获取系统概述文本
     */
    private String getSystemOverview() {
        return """
                医疗保健管理系统概述

                本系统是一个基于MVC架构的医疗保健信息管理系统，提供以下核心功能：

                🏥 患者管理
                • 患者信息注册和管理
                • 患者档案维护
                • GP诊所变更
                • 患者统计分析

                📅 预约管理
                • 预约创建和取消
                • 预约重新安排
                • 时间冲突检查
                • 预约提醒功能

                💊 处方管理
                • 处方开具和发放
                • 处方续方处理
                • 到药状态跟踪
                • 过期处方提醒

                🔄 转诊管理
                • 转诊流程管理
                • 优先级设置和升级
                • 转诊状态跟踪
                • 工作流管理

                📊 系统特性
                • 基于CSV的数据持久化
                • 实时数据验证
                • 完整的业务规则检查
                • 现代化的Swing GUI界面
                • 模块化的架构设计

                🛡️ 数据安全
                • NHS号码等敏感信息保护
                • 业务规则约束
                • 数据完整性验证
                • 操作日志记录

                开始使用：请从左侧导航栏选择相应的功能模块。
                """;
    }

    /**
     * 刷新所有面板数据
     */
    public void refreshAllData() {
        if (patientPanel != null) patientPanel.refreshData();
        if (appointmentPanel != null) appointmentPanel.refreshData();
        if (prescriptionPanel != null) prescriptionPanel.refreshData();
        if (referralPanel != null) referralPanel.refreshData();
    }

    /**
     * 获取当前显示的面板名称
     */
    public String getCurrentPanelName() {
        // 这个方法可能需要根据CardLayout的当前状态来实现
        // 这里暂时返回一个默认值
        return "HOME";
    }

    // 临时面板类 - 后续会被具体的实现替换
    private static class PatientPanel extends BasePanel {
        public PatientPanel(HealthcareController controller, HealthcareView parentView) {
            super(controller, parentView);
        }
        @Override protected void initializeComponents() {}
        @Override protected void setupLayout() {
            add(GuiUtils.createLabel("患者管理面板 - 开发中"));
        }
        @Override protected void setupEventHandlers() {}
        @Override public void refreshData() {}
        @Override public void clearForm() {}
        @Override public String getPanelTitle() { return "患者管理"; }
    }

    private static class AppointmentPanel extends BasePanel {
        public AppointmentPanel(HealthcareController controller, HealthcareView parentView) {
            super(controller, parentView);
        }
        @Override protected void initializeComponents() {}
        @Override protected void setupLayout() {
            add(GuiUtils.createLabel("预约管理面板 - 开发中"));
        }
        @Override protected void setupEventHandlers() {}
        @Override public void refreshData() {}
        @Override public void clearForm() {}
        @Override public String getPanelTitle() { return "预约管理"; }
    }

    private static class PrescriptionPanel extends BasePanel {
        public PrescriptionPanel(HealthcareController controller, HealthcareView parentView) {
            super(controller, parentView);
        }
        @Override protected void initializeComponents() {}
        @Override protected void setupLayout() {
            add(GuiUtils.createLabel("处方管理面板 - 开发中"));
        }
        @Override protected void setupEventHandlers() {}
        @Override public void refreshData() {}
        @Override public void clearForm() {}
        @Override public String getPanelTitle() { return "处方管理"; }
    }

    private static class ReferralPanel extends BasePanel {
        public ReferralPanel(HealthcareController controller, HealthcareView parentView) {
            super(controller, parentView);
        }
        @Override protected void initializeComponents() {}
        @Override protected void setupLayout() {
            add(GuiUtils.createLabel("转诊管理面板 - 开发中"));
        }
        @Override protected void setupEventHandlers() {}
        @Override public void refreshData() {}
        @Override public void clearForm() {}
        @Override public String getPanelTitle() { return "转诊管理"; }
    }
}
