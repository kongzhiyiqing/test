package com.healthcare.repository;

import com.healthcare.model.Clinician;
import com.healthcare.util.CsvUtil;
import com.healthcare.util.HealthcareException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 医生数据仓库
 * 处理医生数据的CSV文件存储
 *
 * @author Healthcare System
 * @version 1.0
 */
public class ClinicianRepository extends CsvDataRepository<Clinician> {

    private static final String CSV_HEADER = "clinician_id,first_name,last_name,title,speciality,gmc_number,phone_number,email,workplace_id,workplace_type,employment_status,start_date";

    /**
     * 构造函数
     */
    public ClinicianRepository() {
        super("clinicians.csv");
    }

    /**
     * 构造函数（指定文件路径）
     */
    public ClinicianRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected String getCsvHeader() {
        return CSV_HEADER;
    }

    @Override
    protected String entityToCsvRow(Clinician clinician) {
        return CsvUtil.formatCsvRow(
                clinician.getId(),
                clinician.getFirstName(),
                clinician.getLastName(),
                clinician.getTitle(),
                clinician.getSpeciality(),
                clinician.getGmcNumber(),
                clinician.getPhoneNumber(),
                clinician.getEmail(),
                clinician.getWorkplaceId(),
                clinician.getWorkplaceType(),
                clinician.getEmploymentStatus(),
                CsvUtil.formatDate(clinician.getStartDate())
        );
    }

    @Override
    protected Clinician csvRowToEntity(String csvRow) throws HealthcareException {
        String[] fields = CsvUtil.parseCsvRow(csvRow);

        if (fields.length < 12) {
            throw new HealthcareException("CSV行格式错误: 字段数量不足，需要12个字段，实际" + fields.length + "个");
        }

        try {
            String id = CsvUtil.emptyToNull(fields[0]);
            String firstName = CsvUtil.emptyToNull(fields[1]);
            String lastName = CsvUtil.emptyToNull(fields[2]);
            String title = CsvUtil.emptyToNull(fields[3]);
            String speciality = CsvUtil.emptyToNull(fields[4]);
            String gmcNumber = CsvUtil.emptyToNull(fields[5]);
            String phoneNumber = CsvUtil.emptyToNull(fields[6]);
            String email = CsvUtil.emptyToNull(fields[7]);
            String workplaceId = CsvUtil.emptyToNull(fields[8]);
            String workplaceType = CsvUtil.emptyToNull(fields[9]);
            String employmentStatus = CsvUtil.emptyToNull(fields[10]);
            LocalDate startDate = CsvUtil.parseDate(fields[11]);

            return new Clinician(id, firstName, lastName, title, speciality, gmcNumber,
                               phoneNumber, email, workplaceId, workplaceType,
                               employmentStatus, startDate);

        } catch (Exception e) {
            throw new HealthcareException("解析医生数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据工作场所ID查找医生
     */
    public List<Clinician> findByWorkplaceId(String workplaceId) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(clinician -> workplaceId != null && workplaceId.equals(clinician.getWorkplaceId()))
                .collect(Collectors.toList());
    }

    /**
     * 根据专科查找医生
     */
    public List<Clinician> findBySpeciality(String speciality) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(clinician -> speciality != null && speciality.equals(clinician.getSpeciality()))
                .collect(Collectors.toList());
    }

    /**
     * 根据职称查找医生
     */
    public List<Clinician> findByTitle(String title) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(clinician -> title != null && title.equals(clinician.getTitle()))
                .collect(Collectors.toList());
    }

    /**
     * 根据工作场所类型查找医生
     */
    public List<Clinician> findByWorkplaceType(String workplaceType) throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(clinician -> workplaceType != null && workplaceType.equals(clinician.getWorkplaceType()))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有全职医生
     */
    public List<Clinician> findAllFullTimeClinicians() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(Clinician::isFullTime)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有医院医生
     */
    public List<Clinician> findAllHospitalClinicians() throws HealthcareException {
        return findByWorkplaceType("Hospital");
    }

    /**
     * 获取所有GP诊所医生
     */
    public List<Clinician> findAllGPSurgeryClinicians() throws HealthcareException {
        return findByWorkplaceType("GP Surgery");
    }

    /**
     * 搜索医生（按姓名）
     */
    public List<Clinician> searchByName(String name) throws HealthcareException {
        ensureLoaded();

        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }

        String searchTerm = name.toLowerCase().trim();
        return cache.stream()
                .filter(clinician -> clinician.getFullName().toLowerCase().contains(searchTerm) ||
                                   clinician.getFirstName().toLowerCase().contains(searchTerm) ||
                                   clinician.getLastName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * 获取医生统计信息
     */
    public String getClinicianStatistics() throws HealthcareException {
        ensureLoaded();

        int totalClinicians = cache.size();
        long fullTime = cache.stream().filter(Clinician::isFullTime).count();
        long partTime = cache.size() - fullTime;
        long hospitalStaff = cache.stream().filter(Clinician::isHospitalStaff).count();
        long gpStaff = cache.stream().filter(Clinician::isGPSurgeryStaff).count();

        // 计算平均工作年限
        double avgYearsOfService = cache.stream()
                .filter(clinician -> clinician.getStartDate() != null)
                .mapToInt(Clinician::getYearsOfService)
                .average()
                .orElse(0.0);

        // 获取专科分布
        java.util.Map<String, Long> specialityCount = cache.stream()
                .filter(clinician -> clinician.getSpeciality() != null)
                .collect(Collectors.groupingBy(Clinician::getSpeciality, Collectors.counting()));

        StringBuilder stats = new StringBuilder();
        stats.append(String.format("医生统计:\n总医生数: %d\n全职医生: %d\n兼职医生: %d\n医院医生: %d\nGP诊所医生: %d\n平均工作年限: %.1f\n\n专科分布:\n",
                totalClinicians, fullTime, partTime, hospitalStaff, gpStaff, avgYearsOfService));

        specialityCount.entrySet().stream()
                .sorted(java.util.Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> stats.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));

        return stats.toString();
    }

    /**
     * 获取所有专科列表
     */
    public java.util.Set<String> getAllSpecialities() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .filter(clinician -> clinician.getSpeciality() != null)
                .map(Clinician::getSpeciality)
                .collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new));
    }

    /**
     * 获取按工作年限排序的医生列表
     */
    public List<Clinician> findAllOrderedByYearsOfService() throws HealthcareException {
        ensureLoaded();

        return cache.stream()
                .sorted((c1, c2) -> Integer.compare(c2.getYearsOfService(), c1.getYearsOfService()))
                .collect(Collectors.toList());
    }
}
