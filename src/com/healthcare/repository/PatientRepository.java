package com.healthcare.repository;

import com.healthcare.model.Patient;
import com.healthcare.util.CsvUtil;
import com.healthcare.util.HealthcareException;

import java.time.LocalDate;

/**
 * 患者数据仓库
 * 处理患者数据的CSV文件存储
 *
 * @author Healthcare System
 * @version 1.0
 */
public class PatientRepository extends CsvDataRepository<Patient> {

    private static final String CSV_HEADER = "patient_id,first_name,last_name,date_of_birth,nhs_number,gender,phone_number,email,address,postcode,emergency_contact_name,emergency_contact_phone,registration_date,gp_surgery_id";

    /**
     * 构造函数
     */
    public PatientRepository() {
        super("patients.csv");
    }

    /**
     * 构造函数（指定文件路径）
     */
    public PatientRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected String getCsvHeader() {
        return CSV_HEADER;
    }

    @Override
    protected String entityToCsvRow(Patient patient) {
        return CsvUtil.formatCsvRow(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                CsvUtil.formatDate(patient.getDateOfBirth()),
                patient.getNhsNumber(),
                patient.getGender(),
                patient.getPhoneNumber(),
                patient.getEmail(),
                patient.getAddress(),
                patient.getPostcode(),
                patient.getEmergencyContactName(),
                patient.getEmergencyContactPhone(),
                CsvUtil.formatDate(patient.getRegistrationDate()),
                patient.getGpSurgeryId()
        );
    }

    @Override
    protected Patient csvRowToEntity(String csvRow) throws HealthcareException {
        String[] fields = CsvUtil.parseCsvRow(csvRow);

        if (fields.length < 14) {
            throw new HealthcareException("CSV行格式错误: 字段数量不足，需要14个字段，实际" + fields.length + "个");
        }

        try {
            String id = CsvUtil.emptyToNull(fields[0]);
            String firstName = CsvUtil.emptyToNull(fields[1]);
            String lastName = CsvUtil.emptyToNull(fields[2]);
            LocalDate dateOfBirth = CsvUtil.parseDate(fields[3]);
            String nhsNumber = CsvUtil.emptyToNull(fields[4]);
            String gender = CsvUtil.emptyToNull(fields[5]);
            String phoneNumber = CsvUtil.emptyToNull(fields[6]);
            String email = CsvUtil.emptyToNull(fields[7]);
            String address = CsvUtil.emptyToNull(fields[8]);
            String postcode = CsvUtil.emptyToNull(fields[9]);
            String emergencyContactName = CsvUtil.emptyToNull(fields[10]);
            String emergencyContactPhone = CsvUtil.emptyToNull(fields[11]);
            LocalDate registrationDate = CsvUtil.parseDate(fields[12]);
            String gpSurgeryId = CsvUtil.emptyToNull(fields[13]);

            return new Patient(id, firstName, lastName, dateOfBirth, nhsNumber, gender,
                             phoneNumber, email, address, postcode, emergencyContactName,
                             emergencyContactPhone, registrationDate, gpSurgeryId);

        } catch (Exception e) {
            throw new HealthcareException("解析患者数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据NHS号码查找患者
     */
    public Patient findByNhsNumber(String nhsNumber) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(patient -> nhsNumber != null && nhsNumber.equals(patient.getNhsNumber()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据GP诊所ID查找患者
     */
    public java.util.List<Patient> findByGpSurgeryId(String gpSurgeryId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(patient -> gpSurgeryId != null && gpSurgeryId.equals(patient.getGpSurgeryId()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 搜索患者（按姓名）
     */
    public java.util.List<Patient> searchByName(String name) throws HealthcareException {
        ensureLoaded();

        if (name == null || name.trim().isEmpty()) {
            return new java.util.ArrayList<>(cache);
        }

        String searchTerm = name.toLowerCase().trim();
        return cache.stream()
                .filter(patient -> {
                    String fullName = patient.getFullName();
                    return fullName != null && fullName.toLowerCase().contains(searchTerm);
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取年龄统计
     */
    public String getAgeStatistics() throws HealthcareException {
        ensureLoaded();

        if (cache.isEmpty()) {
            return "暂无患者数据";
        }

        long children = cache.stream().mapToLong(Patient::getAge).filter(age -> age < 18).count();
        long adults = cache.stream().mapToLong(Patient::getAge).filter(age -> age >= 18 && age < 65).count();
        long seniors = cache.stream().mapToLong(Patient::getAge).filter(age -> age >= 65).count();
        double avgAge = cache.stream().mapToLong(Patient::getAge).average().orElse(0.0);

        return String.format("患者年龄统计:\n总患者数: %d\n儿童(0-17): %d\n成人(18-64): %d\n老人(65+): %d\n平均年龄: %.1f",
                cache.size(), children, adults, seniors, avgAge);
    }
}
