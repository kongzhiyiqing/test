package com.healthcare.view;

import com.healthcare.controller.HealthcareController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 医疗保健管理系统视图
 * 基于Swing的图形用户界面
 *
 * @author Healthcare System
 * @version 1.0
 */
public class HealthcareView extends JFrame {

    private HealthcareController controller;
    private JPanel mainPanel;
    private JButton patientButton;
    private JButton appointmentButton;
    private JButton prescriptionButton;
    private JButton referralButton;
    private JButton exitButton;

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
        setTitle("医疗保健管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new GridBagLayout());
        patientButton = new JButton("患者管理");
        appointmentButton = new JButton("预约管理");
        prescriptionButton = new JButton("处方管理");
        referralButton = new JButton("转诊管理");
        exitButton = new JButton("退出系统");

        // 设置按钮样式
        Dimension buttonSize = new Dimension(200, 40);
        patientButton.setPreferredSize(buttonSize);
        appointmentButton.setPreferredSize(buttonSize);
        prescriptionButton.setPreferredSize(buttonSize);
        referralButton.setPreferredSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 标题
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(new JLabel("医疗保健管理系统"), gbc);

        // 按钮区域
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(patientButton, gbc);

        gbc.gridy = 2;
        mainPanel.add(appointmentButton, gbc);

        gbc.gridy = 3;
        mainPanel.add(prescriptionButton, gbc);

        gbc.gridy = 4;
        mainPanel.add(referralButton, gbc);

        gbc.gridy = 5;
        mainPanel.add(exitButton, gbc);

        add(mainPanel);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        patientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.handlePatientManagement();
            }
        });

        appointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.handleAppointmentManagement();
            }
        });

        prescriptionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.handlePrescriptionManagement();
            }
        });

        referralButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.handleReferralManagement();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.exitSystem();
            }
        });
    }

    /**
     * 显示主菜单
     */
    public void showMainMenu() {
        setVisible(true);
    }

    /**
     * 显示信息对话框
     */
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "信息", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示错误对话框
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}
