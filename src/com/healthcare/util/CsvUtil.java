package com.healthcare.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * CSV工具类
 * 提供CSV数据解析和格式化的工具方法
 *
 * @author Healthcare System
 * @version 1.0
 */
public class CsvUtil {

    // 日期时间格式
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 解析CSV行，按逗号分割并处理引号
     */
    public static String[] parseCsvRow(String csvRow) {
        if (csvRow == null || csvRow.trim().isEmpty()) {
            return new String[0];
        }

        java.util.List<String> fields = new java.util.ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < csvRow.length(); i++) {
            char c = csvRow.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < csvRow.length() && csvRow.charAt(i + 1) == '"') {
                    // 转义的引号
                    currentField.append('"');
                    i++; // 跳过下一个引号
                } else {
                    // 切换引号状态
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // 字段结束
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }

        // 添加最后一个字段
        fields.add(currentField.toString().trim());

        return fields.toArray(new String[0]);
    }

    /**
     * 将字符串数组格式化为CSV行
     */
    public static String formatCsvRow(String... fields) {
        if (fields == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(",");
            }

            String field = fields[i] != null ? fields[i] : "";

            // 如果字段包含逗号、引号或换行符，需要用引号包围
            if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
                sb.append("\"").append(field.replace("\"", "\"\"")).append("\"");
            } else {
                sb.append(field);
            }
        }

        return sb.toString();
    }

    /**
     * 解析日期字符串
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            System.err.println("解析日期失败: " + dateStr);
            return null;
        }
    }

    /**
     * 格式化日期为字符串
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMAT) : "";
    }

    /**
     * 解析时间字符串
     */
    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalTime.parse(timeStr.trim(), TIME_FORMAT);
        } catch (DateTimeParseException e) {
            System.err.println("解析时间失败: " + timeStr);
            return null;
        }
    }

    /**
     * 格式化时间为字符串
     */
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMAT) : "";
    }

    /**
     * 解析日期时间字符串
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateTimeStr.trim(), DATETIME_FORMAT);
        } catch (DateTimeParseException e) {
            System.err.println("解析日期时间失败: " + dateTimeStr);
            return null;
        }
    }

    /**
     * 格式化日期时间为字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMAT) : "";
    }

    /**
     * 安全解析整数
     */
    public static Integer parseInteger(String intStr) {
        if (intStr == null || intStr.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(intStr.trim());
        } catch (NumberFormatException e) {
            System.err.println("解析整数失败: " + intStr);
            return null;
        }
    }

    /**
     * 格式化整数为字符串
     */
    public static String formatInteger(Integer value) {
        return value != null ? value.toString() : "";
    }

    /**
     * 安全解析布尔值
     */
    public static Boolean parseBoolean(String boolStr) {
        if (boolStr == null || boolStr.trim().isEmpty()) {
            return null;
        }

        String value = boolStr.trim().toLowerCase();
        return "true".equals(value) || "1".equals(value) || "yes".equals(value);
    }

    /**
     * 格式化布尔值为字符串
     */
    public static String formatBoolean(Boolean value) {
        return value != null ? value.toString() : "";
    }

    /**
     * 处理CSV字段中的null值
     */
    public static String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    /**
     * 处理空字符串为null
     */
    public static String emptyToNull(String value) {
        return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
    }
}
