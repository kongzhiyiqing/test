package com.healthcare.repository;

import com.healthcare.model.Staff;
import com.healthcare.util.CsvUtil;
import com.healthcare.util.HealthcareException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作人员数据仓库
 * 处理工作人员数据的CSV文件存储
 *
 * @author Healthcare System
 * @version 1.0
 */
public class StaffRepository extends CsvDataRepository<Staff> {

    private static final String CSV_HEADER = "staff_id,first_name,last_name,role,department,facility_id,phone_number,email,employment_status,start_date,line_manager,access_level";

    /**
     * 构造函数
     */
    public StaffRepository() {
        super("staff.csv");
    }

    /**
     * 构造函数（指定文件路径）
     */
    public StaffRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected String getCsvHeader() {
        return CSV_HEADER;
    }

    @Override
    protected String entityToCsvRow(Staff staff) {
        return CsvUtil.formatCsvRow(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getRole(),
                staff.getDepartment(),
                staff.getFacilityId(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                staff.getEmploymentStatus(),
                CsvUtil.formatDate(staff.getStartDate()),
                staff.getLineManager(),
                staff.getAccessLevel()
        );
    }

    @Override
    protected Staff csvRowToEntity(String csvRow) throws HealthcareException {
        String[] fields = CsvUtil.parseCsvRow(csvRow);

        if (fields.length < 12) {
            throw new HealthcareException("CSV行格式错误: 字段数量不足，需要12个字段，实际" + fields.length + "个");
        }

        try {
            String id = CsvUtil.emptyToNull(fields[0]);
            String firstName = CsvUtil.emptyToNull(fields[1]);
            String lastName = CsvUtil.emptyToNull(fields[2]);
            String role = CsvUtil.emptyToNull(fields[3]);
            String department = CsvUtil.emptyToNull(fields[4]);
            String facilityId = CsvUtil.emptyToNull(fields[5]);
            String phoneNumber = CsvUtil.emptyToNull(fields[6]);
            String email = CsvUtil.emptyToNull(fields[7]);
            String employmentStatus = CsvUtil.emptyToNull(fields[8]);
            LocalDate startDate = CsvUtil.parseDate(fields[9]);
            String lineManager = CsvUtil.emptyToNull(fields[10]);
            String accessLevel = CsvUtil.emptyToNull(fields[11]);

            return new Staff(id, firstName, lastName, role, department, facilityId,
                           phoneNumber, email, employmentStatus, startDate, lineManager, accessLevel);

        } catch (Exception e) {
            throw new HealthcareException("解析工作人员数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据设施ID查找工作人员
     */
    public List<Staff> findByFacilityId(String facilityId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(staff -> facilityId != null && facilityId.equals(staff.getFacilityId()))
                .collect(Collectors.toList());
    }

    /**
     * 根据角色查找工作人员
     */
    public List<Staff> findByRole(String role) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(staff -> role != null && role.equals(staff.getRole()))
                .collect(Collectors.toList());
    }

    /**
     * 根据部门查找工作人员
     */
    public List<Staff> findByDepartment(String department) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(staff -> department != null && department.equals(staff.getDepartment()))
                .collect(Collectors.toList());
    }

    /**
     * 根据上级管理者查找工作人员
     */
    public List<Staff> findByLineManager(String lineManager) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(staff -> lineManager != null && lineManager.equals(staff.getLineManager()))
                .collect(Collectors.toList());
    }

    /**
     * 根据访问级别查找工作人员
     */
    public List<Staff> findByAccessLevel(String accessLevel) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(staff -> accessLevel != null && accessLevel.equals(staff.getAccessLevel()))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有全职员工
     */
    public List<Staff> findAllFullTimeStaff() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(Staff::isFullTime)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有兼职员工
     */
    public List<Staff> findAllPartTimeStaff() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(Staff::isPartTime)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有管理人员
     */
    public List<Staff> findAllManagers() throws HealthcareException {
        return findByAccessLevel("Manager");
    }

    /**
     * 获取所有接待员
     */
    public List<Staff> findAllReceptionists() throws HealthcareException {
        return findByRole("Receptionist");
    }

    /**
     * 获取所有医疗秘书
     */
    public List<Staff> findAllMedicalSecretaries() throws HealthcareException {
        return findByRole("Medical Secretary");
    }

    /**
     * 搜索工作人员（按姓名）
     */
    public List<Staff> searchByName(String name) throws HealthcareException {
        ensureLoaded();

        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }

        String searchTerm = name.toLowerCase().trim();
        return cache.stream()
                .filter(staff -> staff.getFullName().toLowerCase().contains(searchTerm) ||
                                staff.getFirstName().toLowerCase().contains(searchTerm) ||
                                staff.getLastName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * 获取工作人员统计信息
     */
    public String getStaffStatistics() throws HealthcareException {
        ensureLoaded();

        int totalStaff = cache.size();
        long fullTime = cache.stream().filter(Staff::isFullTime).count();
        long partTime = cache.stream().filter(Staff::isPartTime).count();
        long managers = cache.stream().filter(Staff::isManager).count();
        long standardUsers = cache.stream().filter(Staff::isStandardUser).count();
        long basicUsers = cache.stream().filter(Staff::isBasicUser).count();

        // 计算平均工作年限
        double avgYearsOfService = cache.stream()
                .filter(staff -> staff.getStartDate() != null)
                .mapToInt(Staff::getYearsOfService)
                .average()
                .orElse(0.0);

        return String.format("工作人员统计:\n总员工数: %d\n全职员工: %d\n兼职员工: %d\n管理人员: %d\n标准用户: %d\n基本用户: %d\n平均工作年限: %.1f",
                totalStaff, fullTime, partTime, managers, standardUsers, basicUsers, avgYearsOfService);
    }

    /**
     * 获取所有部门列表
     */
    public java.util.Set<String> getAllDepartments() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(staff -> staff.getDepartment() != null)
                .map(Staff::getDepartment)
                .collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new));
    }

    /**
     * 获取所有角色列表
     */
    public java.util.Set<String> getAllRoles() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(staff -> staff.getRole() != null)
                .map(Staff::getRole)
                .collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new));
    }

    /**
     * 获取按工作年限排序的员工列表
     */
    public List<Staff> findAllOrderedByYearsOfService() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .sorted((s1, s2) -> Integer.compare(s2.getYearsOfService(), s1.getYearsOfService()))
                .collect(Collectors.toList());
    }
}
