package com.healthcare.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 基础对话框
 * 提供通用对话框功能
 *
 * @author Healthcare System
 * @version 1.0
 */
public abstract class BaseDialog extends JDialog {

    protected JPanel mainPanel;
    protected JPanel buttonPanel;
    protected JButton okButton;
    protected JButton cancelButton;
    protected boolean confirmed = false;

    /**
     * 构造函数
     */
    public BaseDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        mainPanel = GuiUtils.createPanel();
        buttonPanel = GuiUtils.createPanel(new FlowLayout(FlowLayout.RIGHT));

        okButton = GuiUtils.createButton("确定", e -> onOk());
        cancelButton = GuiUtils.createSecondaryButton("取消", e -> onCancel());

        // 创建具体的对话框内容
        createContent();
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());

        // 添加主内容面板
        add(GuiUtils.createScrollPane(mainPanel), BorderLayout.CENTER);

        // 添加按钮面板
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        GuiUtils.centerWindow(this);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        // ESC键关闭对话框
        getRootPane().registerKeyboardAction(
            e -> onCancel(),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Enter键确认
        getRootPane().registerKeyboardAction(
            e -> onOk(),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    /**
     * 创建对话框内容（子类实现）
     */
    protected abstract void createContent();

    /**
     * 确定按钮点击事件
     */
    protected void onOk() {
        if (validateInput()) {
            confirmed = true;
            saveData();
            dispose();
        }
    }

    /**
     * 取消按钮点击事件
     */
    protected void onCancel() {
        confirmed = false;
        dispose();
    }

    /**
     * 验证输入数据（子类实现）
     */
    protected abstract boolean validateInput();

    /**
     * 保存数据（子类实现）
     */
    protected abstract void saveData();

    /**
     * 显示对话框并返回确认状态
     */
    public boolean showDialog() {
        setVisible(true);
        return confirmed;
    }

    /**
     * 设置确定按钮文本
     */
    protected void setOkButtonText(String text) {
        okButton.setText(text);
    }

    /**
     * 设置取消按钮文本
     */
    protected void setCancelButtonText(String text) {
        cancelButton.setText(text);
    }

    /**
     * 启用/禁用确定按钮
     */
    protected void setOkButtonEnabled(boolean enabled) {
        okButton.setEnabled(enabled);
    }

    /**
     * 显示错误消息
     */
    protected void showError(String message) {
        GuiUtils.showErrorMessage(this, message, "错误");
    }

    /**
     * 显示警告消息
     */
    protected void showWarning(String message) {
        GuiUtils.showWarningMessage(this, message, "警告");
    }

    /**
     * 显示信息消息
     */
    protected void showInfo(String message) {
        GuiUtils.showInfoMessage(this, message, "信息");
    }
}
