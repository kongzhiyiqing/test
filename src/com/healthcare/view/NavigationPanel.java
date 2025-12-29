package com.healthcare.view;

import com.healthcare.controller.HealthcareController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 导航面板
 * 提供功能模块之间的导航
 *
 * @author Healthcare System
 * @version 1.0
 */
public class NavigationPanel extends JPanel {

    private HealthcareController controller;
    private HealthcareView parentView;
    private JButton currentButton;

    // 导航按钮
    private JButton patientButton;
    private JButton appointmentButton;
    private JButton prescriptionButton;
    private JButton referralButton;
    private JButton homeButton;
    private JButton exitButton;

    /**
     * 构造函数
     */
    public NavigationPanel(HealthcareController controller, HealthcareView parentView) {
        this.controller = controller;
        this.parentView = parentView;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        setBackground(GuiUtils.PRIMARY_COLOR);
        setPreferredSize(new Dimension(200, -1));

        // 创建导航按钮
        homeButton = createNavButton("主页", "🏠");
        patientButton = createNavButton("患者管理", "👤");
        appointmentButton = createNavButton("预约管理", "📅");
        prescriptionButton = createNavButton("处方管理", "💊");
        referralButton = createNavButton("转诊管理", "🔄");
        exitButton = createNavButton("退出系统", "🚪");

        // 设置退出按钮为危险样式
        exitButton.setBackground(GuiUtils.ERROR_COLOR);
        exitButton.setForeground(Color.WHITE);
    }

    /**
     * 创建导航按钮
     */
    private JButton createNavButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(GuiUtils.NORMAL_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180, 200)); // 半透明
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(180, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // 鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != currentButton) {
                    button.setBackground(button.getBackground().brighter());
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != currentButton) {
                    button.setBackground(new Color(70, 130, 180, 200));
                }
            }
        });

        return button;
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 标题
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(GuiUtils.createTitleLabel("医疗保健管理系统"), gbc);

        // 分隔线
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 20, 10);
        add(new JSeparator(), gbc);

        // 导航按钮
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 10, 2, 10);

        gbc.gridy = 2;
        add(homeButton, gbc);

        gbc.gridy = 3;
        add(patientButton, gbc);

        gbc.gridy = 4;
        add(appointmentButton, gbc);

        gbc.gridy = 5;
        add(prescriptionButton, gbc);

        gbc.gridy = 6;
        add(referralButton, gbc);

        // 底部退出按钮
        gbc.gridy = 7;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(20, 10, 10, 10);
        add(exitButton, gbc);

        // 设置主页为当前选中
        setCurrentButton(homeButton);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        homeButton.addActionListener(e -> {
            setCurrentButton(homeButton);
            controller.startSystem();
        });

        patientButton.addActionListener(e -> {
            setCurrentButton(patientButton);
            controller.handlePatientManagement();
        });

        appointmentButton.addActionListener(e -> {
            setCurrentButton(appointmentButton);
            controller.handleAppointmentManagement();
        });

        prescriptionButton.addActionListener(e -> {
            setCurrentButton(prescriptionButton);
            controller.handlePrescriptionManagement();
        });

        referralButton.addActionListener(e -> {
            setCurrentButton(referralButton);
            controller.handleReferralManagement();
        });

        exitButton.addActionListener(e -> controller::exitSystem);
    }

    /**
     * 设置当前选中的按钮
     */
    private void setCurrentButton(JButton button) {
        // 恢复之前按钮的样式
        if (currentButton != null) {
            currentButton.setBackground(new Color(70, 130, 180, 200));
            currentButton.setFont(GuiUtils.NORMAL_FONT);
        }

        // 设置新按钮的样式
        currentButton = button;
        currentButton.setBackground(Color.WHITE);
        currentButton.setForeground(GuiUtils.PRIMARY_COLOR);
        currentButton.setFont(GuiUtils.HEADER_FONT.deriveFont(Font.BOLD));
    }

    /**
     * 获取当前选中的模块
     */
    public String getCurrentModule() {
        if (currentButton == homeButton) return "主页";
        if (currentButton == patientButton) return "患者管理";
        if (currentButton == appointmentButton) return "预约管理";
        if (currentButton == prescriptionButton) return "处方管理";
        if (currentButton == referralButton) return "转诊管理";
        return "未知";
    }

    /**
     * 程序化导航到指定模块
     */
    public void navigateTo(String moduleName) {
        switch (moduleName.toLowerCase()) {
            case "主页":
            case "home":
                setCurrentButton(homeButton);
                controller.startSystem();
                break;
            case "患者管理":
            case "patient":
                setCurrentButton(patientButton);
                controller.handlePatientManagement();
                break;
            case "预约管理":
            case "appointment":
                setCurrentButton(appointmentButton);
                controller.handleAppointmentManagement();
                break;
            case "处方管理":
            case "prescription":
                setCurrentButton(prescriptionButton);
                controller.handlePrescriptionManagement();
                break;
            case "转诊管理":
            case "referral":
                setCurrentButton(referralButton);
                controller.handleReferralManagement();
                break;
        }
    }

    /**
     * 更新导航状态（基于用户权限等）
     */
    public void updateNavigationState(boolean patientEnabled, boolean appointmentEnabled,
                                    boolean prescriptionEnabled, boolean referralEnabled) {
        patientButton.setEnabled(patientEnabled);
        appointmentButton.setEnabled(appointmentEnabled);
        prescriptionButton.setEnabled(prescriptionEnabled);
        referralButton.setEnabled(referralEnabled);
    }
}
