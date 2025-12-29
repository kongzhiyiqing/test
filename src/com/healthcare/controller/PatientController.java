package com.healthcare.controller;

import com.healthcare.model.Patient;
import com.healthcare.util.HealthcareException;
import com.healthcare.view.HealthcareView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 患者管理控制器
 * 处理患者相关的业务逻辑
 *
 * @author Healthcare System
 * @version 1.0
 */
public class PatientController extends BaseController {

    private List<Patient> patients;

    /**
     * 构造函数
     */
    public PatientController() {
        this.patients = new ArrayList<>();
        initializeSampleData();
    }

    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        // 添加一些示例患者数据
        patients.add(new Patient("P001", "John", "Smith", LocalDate.of(1985, 3, 15),
                "1234567890", "M", "07123456789", "john.smith@email.com",
                "123 Oak Street, Birmingham", "B1 1AA", "Sarah Smith", "07987654321",
                LocalDate.of(2020, 1, 15), "S001"));

        patients.add(new Patient("P002", "Emma", "Johnson", LocalDate.of(1990, 7, 22),
                "2345678901", "F", "07234567890", "emma.johnson@email.com",
                "456 Pine Avenue, Birmingham", "B2 2BB", "Michael Johnson", "07876543210",
                LocalDate.of(2019, 5, 10), "S001"));
    }

    /**
     * 获取所有患者
     */
    public List<Patient> getAllPatients() {
        logOperation("getAllPatients", "获取所有患者列表");
        return new ArrayList<>(patients);
    }

    /**
     * 根据ID查找患者
     */
    public Optional<Patient> findPatientById(String patientId) {
        validateNotEmpty(patientId, "patientId");
        logOperation("findPatientById", "查找患者ID: " + patientId);

        return patients.stream()
                .filter(patient -> patientId.equals(patient.getId()))
                .findFirst();
    }

    /**
     * 根据NHS号码查找患者
     */
    public Optional<Patient> findPatientByNhsNumber(String nhsNumber) {
        validateNotEmpty(nhsNumber, "nhsNumber");
        logOperation("findPatientByNhsNumber", "查找NHS号码: " + nhsNumber);

        return patients.stream()
                .filter(patient -> nhsNumber.equals(patient.getNhsNumber()))
                .findFirst();
    }

    /**
     * 添加新患者
     */
    public void addPatient(Patient patient) throws HealthcareException {
        validateNotNull(patient, "patient");
        if (!patient.isValid()) {
            throw HealthcareException.validationError("patient", "患者数据无效");
        }

        // 检查ID是否已存在
        if (findPatientById(patient.getId()).isPresent()) {
            throw HealthcareException.businessLogicError("addPatient", "患者ID已存在: " + patient.getId());
        }

        // 检查NHS号码是否已存在
        if (findPatientByNhsNumber(patient.getNhsNumber()).isPresent()) {
            throw HealthcareException.businessLogicError("addPatient", "NHS号码已存在: " + patient.getNhsNumber());
        }

        patients.add(patient);
        logOperation("addPatient", "添加新患者: " + patient.getFullName());
        showMessage("患者添加成功: " + patient.getFullName());
    }

    /**
     * 更新患者信息
     */
    public void updatePatient(Patient updatedPatient) throws HealthcareException {
        validateNotNull(updatedPatient, "updatedPatient");

        Optional<Patient> existingPatient = findPatientById(updatedPatient.getId());
        if (!existingPatient.isPresent()) {
            throw HealthcareException.businessLogicError("updatePatient", "患者不存在: " + updatedPatient.getId());
        }

        if (!updatedPatient.isValid()) {
            throw HealthcareException.validationError("updatedPatient", "患者数据无效");
        }

        // 移除旧记录，添加新记录
        patients.remove(existingPatient.get());
        patients.add(updatedPatient);

        logOperation("updatePatient", "更新患者信息: " + updatedPatient.getFullName());
        showMessage("患者信息更新成功: " + updatedPatient.getFullName());
    }

    /**
     * 删除患者
     */
    public void deletePatient(String patientId) throws HealthcareException {
        validateNotEmpty(patientId, "patientId");

        Optional<Patient> patient = findPatientById(patientId);
        if (!patient.isPresent()) {
            throw HealthcareException.businessLogicError("deletePatient", "患者不存在: " + patientId);
        }

        patients.remove(patient.get());
        logOperation("deletePatient", "删除患者: " + patient.get().getFullName());
        showMessage("患者删除成功: " + patient.get().getFullName());
    }

    /**
     * 搜索患者（按姓名）
     */
    public List<Patient> searchPatientsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllPatients();
        }

        String searchTerm = name.toLowerCase().trim();
        return patients.stream()
                .filter(patient -> patient.getFullName().toLowerCase().contains(searchTerm) ||
                                 patient.getFirstName().toLowerCase().contains(searchTerm) ||
                                 patient.getLastName().toLowerCase().contains(searchTerm))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * 获取患者年龄统计
     */
    public String getPatientAgeStatistics() {
        if (patients.isEmpty()) {
            return "暂无患者数据";
        }

        int totalPatients = patients.size();
        long children = patients.stream().mapToInt(Patient::getAge).filter(age -> age < 18).count();
        long adults = patients.stream().mapToInt(Patient::getAge).filter(age -> age >= 18 && age < 65).count();
        long seniors = patients.stream().mapToInt(Patient::getAge).filter(age -> age >= 65).count();

        double avgAge = patients.stream().mapToInt(Patient::getAge).average().orElse(0.0);

        return String.format("患者年龄统计:\n总患者数: %d\n儿童(0-17): %d\n成人(18-64): %d\n老人(65+): %d\n平均年龄: %.1f",
                totalPatients, children, adults, seniors, avgAge);
    }

    /**
     * 获取患者数量
     */
    public int getPatientCount() {
        return patients.size();
    }

    @Override
    public void initialize() {
        logOperation("initialize", "患者控制器初始化完成");
    }

    @Override
    public void cleanup() {
        logOperation("cleanup", "患者控制器清理完成");
        patients.clear();
    }
}
