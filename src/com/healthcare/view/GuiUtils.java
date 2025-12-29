package com.healthcare.view;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * GUI工具类
 * 提供Swing GUI的通用工具方法
 *
 * @author Healthcare System
 * @version 1.0
 */
public class GuiUtils {

    // 颜色常量
    public static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    public static final Color SECONDARY_COLOR = new Color(100, 149, 237);
    public static final Color SUCCESS_COLOR = new Color(34, 139, 34);
    public static final Color WARNING_COLOR = new Color(255, 140, 0);
    public static final Color ERROR_COLOR = new Color(220, 20, 60);
    public static final Color BACKGROUND_COLOR = new Color(248, 248, 248);
    public static final Color PANEL_BACKGROUND = Color.WHITE;

    // 字体常量
    public static final Font TITLE_FONT = new Font("微软雅黑", Font.BOLD, 18);
    public static final Font HEADER_FONT = new Font("微软雅黑", Font.BOLD, 14);
    public static final Font NORMAL_FONT = new Font("微软雅黑", Font.PLAIN, 12);
    public static final Font SMALL_FONT = new Font("微软雅黑", Font.PLAIN, 10);

    /**
     * 创建标准按钮
     */
    public static JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(NORMAL_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (listener != null) {
            button.addActionListener(listener);
        }

        // 鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    /**
     * 创建次级按钮
     */
    public static JButton createSecondaryButton(String text, ActionListener listener) {
        JButton button = createButton(text, listener);
        button.setBackground(SECONDARY_COLOR);
        button.setPreferredSize(new Dimension(100, 30));
        return button;
    }

    /**
     * 创建危险操作按钮
     */
    public static JButton createDangerButton(String text, ActionListener listener) {
        JButton button = createButton(text, listener);
        button.setBackground(ERROR_COLOR);
        return button;
    }

    /**
     * 创建标签
     */
    public static JLabel createLabel(String text) {
        return createLabel(text, NORMAL_FONT);
    }

    /**
     * 创建指定字体的标签
     */
    public static JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    /**
     * 创建标题标签
     */
    public static JLabel createTitleLabel(String text) {
        return createLabel(text, TITLE_FONT);
    }

    /**
     * 创建表头标签
     */
    public static JLabel createHeaderLabel(String text) {
        return createLabel(text, HEADER_FONT);
    }

    /**
     * 创建文本字段
     */
    public static JTextField createTextField() {
        return createTextField(20);
    }

    /**
     * 创建指定列数的文本字段
     */
    public static JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(NORMAL_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    /**
     * 创建密码字段
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(NORMAL_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return passwordField;
    }

    /**
     * 创建文本区域
     */
    public static JTextArea createTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setFont(NORMAL_FONT);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textArea;
    }

    /**
     * 创建组合框
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(NORMAL_FONT);
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }

    /**
     * 创建复选框
     */
    public static JCheckBox createCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(NORMAL_FONT);
        checkBox.setBackground(PANEL_BACKGROUND);
        return checkBox;
    }

    /**
     * 创建单选按钮
     */
    public static JRadioButton createRadioButton(String text) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setFont(NORMAL_FONT);
        radioButton.setBackground(PANEL_BACKGROUND);
        return radioButton;
    }

    /**
     * 创建面板
     */
    public static JPanel createPanel() {
        return createPanel(new BorderLayout());
    }

    /**
     * 创建指定布局的面板
     */
    public static JPanel createPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(PANEL_BACKGROUND);
        return panel;
    }

    /**
     * 创建边框面板
     */
    public static JPanel createBorderPanel(String title) {
        JPanel panel = createPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            title,
            0, 0,
            HEADER_FONT,
            PRIMARY_COLOR
        ));
        return panel;
    }

    /**
     * 创建滚动面板
     */
    public static JScrollPane createScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    /**
     * 创建表格
     */
    public static JTable createTable(Object[][] data, String[] columnNames) {
        JTable table = new JTable(data, columnNames);
        table.setFont(SMALL_FONT);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(HEADER_FONT);
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        return table;
    }

    /**
     * 设置窗口居中
     */
    public static void centerWindow(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getSize();
        window.setLocation(
            (screenSize.width - windowSize.width) / 2,
            (screenSize.height - windowSize.height) / 2
        );
    }

    /**
     * 设置窗口图标
     */
    public static void setWindowIcon(Window window, String iconPath) {
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            window.setIconImage(icon.getImage());
        } catch (Exception e) {
            // 忽略图标设置错误
        }
    }

    /**
     * 显示信息对话框
     */
    public static void showInfoMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示警告对话框
     */
    public static void showWarningMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * 显示错误对话框
     */
    public static void showErrorMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 显示确认对话框
     */
    public static boolean showConfirmDialog(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * 显示输入对话框
     */
    public static String showInputDialog(Component parent, String message, String title, String defaultValue) {
        return (String) JOptionPane.showInputDialog(parent, message, title,
            JOptionPane.QUESTION_MESSAGE, null, null, defaultValue);
    }

    /**
     * 创建表单布局的面板
     */
    public static JPanel createFormPanel(Object[][] formFields) {
        JPanel panel = createPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < formFields.length; i++) {
            // 标签
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.0;
            panel.add(createLabel((String) formFields[i][0]), gbc);

            // 组件
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add((Component) formFields[i][1], gbc);
        }

        return panel;
    }

    /**
     * 创建工具栏
     */
    public static JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(PANEL_BACKGROUND);
        toolBar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        return toolBar;
    }

    /**
     * 创建菜单栏
     */
    public static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(PANEL_BACKGROUND);
        return menuBar;
    }

    /**
     * 创建菜单
     */
    public static JMenu createMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(NORMAL_FONT);
        return menu;
    }

    /**
     * 创建菜单项
     */
    public static JMenuItem createMenuItem(String text, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(NORMAL_FONT);
        if (listener != null) {
            menuItem.addActionListener(listener);
        }
        return menuItem;
    }
}
