package com.healthcare.repository;

import com.healthcare.model.Facility;
import com.healthcare.util.CsvUtil;
import com.healthcare.util.HealthcareException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 医疗设施数据仓库
 * 处理医疗设施数据的CSV文件存储
 *
 * @author Healthcare System
 * @version 1.0
 */
public class FacilityRepository extends CsvDataRepository<Facility> {

    private static final String CSV_HEADER = "facility_id,facility_name,facility_type,address,postcode,phone_number,email,opening_hours,manager_name,capacity,specialities_offered";

    /**
     * 构造函数
     */
    public FacilityRepository() {
        super("facilities.csv");
    }

    /**
     * 构造函数（指定文件路径）
     */
    public FacilityRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected String getCsvHeader() {
        return CSV_HEADER;
    }

    @Override
    protected String entityToCsvRow(Facility facility) {
        return CsvUtil.formatCsvRow(
                facility.getId(),
                facility.getFacilityName(),
                facility.getFacilityType(),
                facility.getAddress(),
                facility.getPostcode(),
                facility.getPhoneNumber(),
                facility.getEmail(),
                facility.getOpeningHours(),
                facility.getManagerName(),
                CsvUtil.formatInteger(facility.getCapacity()),
                facility.getSpecialitiesOffered()
        );
    }

    @Override
    protected Facility csvRowToEntity(String csvRow) throws HealthcareException {
        String[] fields = CsvUtil.parseCsvRow(csvRow);

        if (fields.length < 11) {
            throw new HealthcareException("CSV行格式错误: 字段数量不足，需要11个字段，实际" + fields.length + "个");
        }

        try {
            String id = CsvUtil.emptyToNull(fields[0]);
            String facilityName = CsvUtil.emptyToNull(fields[1]);
            String facilityType = CsvUtil.emptyToNull(fields[2]);
            String address = CsvUtil.emptyToNull(fields[3]);
            String postcode = CsvUtil.emptyToNull(fields[4]);
            String phoneNumber = CsvUtil.emptyToNull(fields[5]);
            String email = CsvUtil.emptyToNull(fields[6]);
            String openingHours = CsvUtil.emptyToNull(fields[7]);
            String managerName = CsvUtil.emptyToNull(fields[8]);
            Integer capacity = CsvUtil.parseInteger(fields[9]);
            String specialitiesOffered = CsvUtil.emptyToNull(fields[10]);

            return new Facility(id, facilityName, facilityType, address, postcode,
                              phoneNumber, email, openingHours, managerName,
                              capacity != null ? capacity : 0, specialitiesOffered);

        } catch (Exception e) {
            throw new HealthcareException("解析设施数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据设施类型查找设施
     */
    public List<Facility> findByFacilityType(String facilityType) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(facility -> facilityType != null && facilityType.equals(facility.getFacilityType()))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有医院
     */
    public List<Facility> findAllHospitals() throws HealthcareException {
        return findByFacilityType("Hospital");
    }

    /**
     * 获取所有GP诊所
     */
    public List<Facility> findAllGPSurgeries() throws HealthcareException {
        return findByFacilityType("GP Surgery");
    }

    /**
     * 根据邮编查找设施
     */
    public List<Facility> findByPostcode(String postcode) throws HealthcareException {
        ensureLoaded();

        if (postcode == null || postcode.trim().isEmpty()) {
            return findAll();
        }

        String searchPostcode = postcode.trim().toLowerCase();
        return cache.stream()
                .filter(facility -> facility.getPostcode() != null &&
                                   facility.getPostcode().toLowerCase().contains(searchPostcode))
                .collect(Collectors.toList());
    }

    /**
     * 根据管理者姓名查找设施
     */
    public List<Facility> findByManagerName(String managerName) throws HealthcareException {
        ensureLoaded();

        if (managerName == null || managerName.trim().isEmpty()) {
            return findAll();
        }

        String searchName = managerName.trim().toLowerCase();
        return cache.stream()
                .filter(facility -> facility.getManagerName() != null &&
                                   facility.getManagerName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
    }

    /**
     * 搜索设施（按名称）
     */
    public List<Facility> searchByName(String name) throws HealthcareException {
        ensureLoaded();

        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }

        String searchTerm = name.toLowerCase().trim();
        return cache.stream()
                .filter(facility -> facility.getFacilityName() != null &&
                                   facility.getFacilityName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * 检查设施是否提供特定专科服务
     */
    public List<Facility> findFacilitiesOfferingSpeciality(String speciality) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(facility -> facility.offersSpeciality(speciality))
                .collect(Collectors.toList());
    }

    /**
     * 获取按容量排序的设施列表
     */
    public List<Facility> findAllOrderedByCapacity() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getCapacity(), f1.getCapacity()))
                .collect(Collectors.toList());
    }

    /**
     * 获取24小时开放的设施
     */
    public List<Facility> find24HourFacilities() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(Facility::isOpen24Hours)
                .collect(Collectors.toList());
    }

    /**
     * 获取设施统计信息
     */
    public String getFacilityStatistics() throws HealthcareException {
        ensureLoaded();

        int totalFacilities = cache.size();
        long hospitals = cache.stream().filter(Facility::isHospital).count();
        long gpSurgeries = cache.stream().filter(Facility::isGPSurgery).count();
        long hour24Facilities = cache.stream().filter(Facility::isOpen24Hours).count();

        int totalCapacity = cache.stream().mapToInt(Facility::getCapacity).sum();
        double avgCapacity = cache.isEmpty() ? 0 : (double) totalCapacity / cache.size();

        return String.format("设施统计:\n总设施数: %d\n医院数量: %d\nGP诊所数量: %d\n24小时开放: %d\n总容量: %d\n平均容量: %.1f",
                totalFacilities, hospitals, gpSurgeries, hour24Facilities, totalCapacity, avgCapacity);
    }

    /**
     * 获取所有专科列表
     */
    public java.util.Set<String> getAllSpecialities() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .flatMap(facility -> facility.getSpecialityList().stream())
                .collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new));
    }
}
