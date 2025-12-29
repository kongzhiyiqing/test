package com.healthcare.view;

import com.healthcare.controller.HealthcareController;

import javax.swing.*;
import java.awt.*;

/**
 * 医疗保健管理系统视图
 * 基于Swing的图形用户界面
 *
 * @author Healthcare System
 * @version 1.0
 */
public class HealthcareView extends JFrame {

    private HealthcareController controller;
    private NavigationPanel navigationPanel;
    private ContentPanel contentPanel;
    private JPanel statusPanel;
    private JLabel statusLabel;

    /**
     * 构造函数
     */
    public HealthcareView(HealthcareController controller) {
        this.controller = controller;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        setTitle("医疗保健管理系统 v1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));

        // 设置窗口图标（如果有的话）
        GuiUtils.setWindowIcon(this, "icon.png");

        // 创建面板
        navigationPanel = new NavigationPanel(controller, this);
        contentPanel = new ContentPanel(controller, this);

        // 状态栏
        statusPanel = GuiUtils.createPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel = GuiUtils.createLabel("系统已就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.setPreferredSize(new Dimension(-1, 25));
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());

        // 添加导航面板
        add(navigationPanel, BorderLayout.WEST);

        // 添加内容面板
        add(contentPanel, BorderLayout.CENTER);

        // 添加状态栏
        add(statusPanel, BorderLayout.SOUTH);

        // 居中显示
        GuiUtils.centerWindow(this);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        // 窗口关闭事件
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                controller.exitSystem();
            }
        });
    }

    /**
     * 显示主菜单
     */
    public void showMainMenu() {
        contentPanel.showPanel("HOME");
        updateStatus("显示主页");
    }

    /**
     * 显示患者管理界面
     */
    public void showPatientManagement() {
        contentPanel.showPanel("PATIENT");
        updateStatus("患者管理模块");
    }

    /**
     * 显示预约管理界面
     */
    public void showAppointmentManagement() {
        contentPanel.showPanel("APPOINTMENT");
        updateStatus("预约管理模块");
    }

    /**
     * 显示处方管理界面
     */
    public void showPrescriptionManagement() {
        contentPanel.showPanel("PRESCRIPTION");
        updateStatus("处方管理模块");
    }

    /**
     * 显示转诊管理界面
     */
    public void showReferralManagement() {
        contentPanel.showPanel("REFERRAL");
        updateStatus("转诊管理模块");
    }

    /**
     * 显示信息对话框
     */
    public void showMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            GuiUtils.showInfoMessage(this, message, "信息");
        });
    }

    /**
     * 显示错误对话框
     */
    public void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            GuiUtils.showErrorMessage(this, message, "错误");
        });
    }

    /**
     * 更新状态栏
     */
    public void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
        });
    }

    /**
     * 刷新所有数据
     */
    public void refreshAllData() {
        contentPanel.refreshAllData();
        updateStatus("数据已刷新");
    }

    /**
     * 显示关于对话框
     */
    public void showAboutDialog() {
        SwingUtilities.invokeLater(() -> {
            String aboutMessage = """
                    医疗保健管理系统 v1.0

                    一个基于MVC架构的医疗保健信息管理系统

                    功能特性：
                    • 患者信息管理
                    • 预约调度系统
                    • 处方管理
                    • 转诊协调
                    • 数据统计分析

                    技术栈：
                    • Java Swing GUI
                    • MVC架构模式
                    • CSV数据存储
                    • 模块化设计

                    © 2024 医疗保健管理系统团队
                    """;

            JOptionPane.showMessageDialog(this, aboutMessage, "关于",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * 获取导航面板
     */
    public NavigationPanel getNavigationPanel() {
        return navigationPanel;
    }

    /**
     * 获取内容面板
     */
    public ContentPanel getContentPanel() {
        return contentPanel;
    }
}
