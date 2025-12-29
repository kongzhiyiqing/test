package com.healthcare.view;

import com.healthcare.controller.HealthcareController;

import javax.swing.*;
import java.awt.*;

/**
 * 基础面板
 * 提供通用面板功能
 *
 * @author Healthcare System
 * @version 1.0
 */
public abstract class BasePanel extends JPanel {

    protected HealthcareController controller;
    protected HealthcareView parentView;

    /**
     * 构造函数
     */
    public BasePanel(HealthcareController controller, HealthcareView parentView) {
        this.controller = controller;
        this.parentView = parentView;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    /**
     * 初始化组件
     */
    protected abstract void initializeComponents();

    /**
     * 设置布局
     */
    protected abstract void setupLayout();

    /**
     * 设置事件处理器
     */
    protected abstract void setupEventHandlers();

    /**
     * 刷新数据
     */
    public abstract void refreshData();

    /**
     * 清空表单
     */
    public abstract void clearForm();

    /**
     * 获取面板标题
     */
    public abstract String getPanelTitle();

    /**
     * 显示信息消息
     */
    protected void showMessage(String message) {
        if (parentView != null) {
            parentView.showMessage(message);
        } else {
            GuiUtils.showInfoMessage(this, message, "信息");
        }
    }

    /**
     * 显示错误消息
     */
    protected void showError(String message) {
        if (parentView != null) {
            parentView.showError(message);
        } else {
            GuiUtils.showErrorMessage(this, message, "错误");
        }
    }

    /**
     * 显示确认对话框
     */
    protected boolean showConfirmDialog(String message, String title) {
        return GuiUtils.showConfirmDialog(this, message, title);
    }

    /**
     * 显示输入对话框
     */
    protected String showInputDialog(String message, String title, String defaultValue) {
        return GuiUtils.showInputDialog(this, message, title, defaultValue);
    }

    /**
     * 创建标准表单布局
     */
    protected JPanel createFormPanel(Object[][] formFields) {
        return GuiUtils.createFormPanel(formFields);
    }

    /**
     * 创建工具栏
     */
    protected JToolBar createToolbar() {
        JToolBar toolbar = GuiUtils.createToolBar();

        // 添加通用按钮
        JButton refreshButton = GuiUtils.createSecondaryButton("刷新", e -> refreshData());
        JButton clearButton = GuiUtils.createSecondaryButton("清空", e -> clearForm());

        toolbar.add(refreshButton);
        toolbar.addSeparator();
        toolbar.add(clearButton);

        return toolbar;
    }

    /**
     * 创建状态栏
     */
    protected JPanel createStatusBar() {
        JPanel statusBar = GuiUtils.createPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());

        JLabel statusLabel = GuiUtils.createLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.setPreferredSize(new Dimension(-1, 25));

        return statusBar;
    }

    /**
     * 更新状态栏消息
     */
    protected void updateStatusMessage(String message) {
        // 子类可以重写此方法来更新状态栏
        System.out.println("状态: " + message);
    }

    /**
     * 启用/禁用面板
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        // 递归设置所有子组件
        setEnabledRecursive(this, enabled);
    }

    /**
     * 递归设置组件启用状态
     */
    private void setEnabledRecursive(Component component, boolean enabled) {
        component.setEnabled(enabled);

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                setEnabledRecursive(child, enabled);
            }
        }
    }

    /**
     * 验证必填字段
     */
    protected boolean validateRequiredField(JTextField field, String fieldName) {
        if (field.getText().trim().isEmpty()) {
            showError(fieldName + "不能为空");
            field.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * 验证组合框选择
     */
    protected boolean validateComboBoxSelection(JComboBox<?> comboBox, String fieldName) {
        if (comboBox.getSelectedItem() == null) {
            showError("请选择" + fieldName);
            comboBox.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * 设置面板边距
     */
    protected void setPanelPadding(JPanel panel, int top, int left, int bottom, int right) {
        panel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }
}
