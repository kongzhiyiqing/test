package com.healthcare.model;

import java.util.Arrays;
import java.util.List;

/**
 * 医疗设施实体类
 * 表示医疗保健系统中的医院和诊所
 *
 * @author Healthcare System
 * @version 1.0
 */
public class Facility extends Entity {

    private String facilityName;
    private String facilityType;
    private String address;
    private String postcode;
    private String phoneNumber;
    private String email;
    private String openingHours;
    private String managerName;
    private int capacity;
    private String specialitiesOffered;

    /**
     * 默认构造函数
     */
    public Facility() {
        super();
    }

    /**
     * 构造函数
     */
    public Facility(String id, String facilityName, String facilityType, String address,
                    String postcode, String phoneNumber, String email, String openingHours,
                    String managerName, int capacity, String specialitiesOffered) {
        super(id);
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.address = address;
        this.postcode = postcode;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.openingHours = openingHours;
        this.managerName = managerName;
        this.capacity = capacity;
        this.specialitiesOffered = specialitiesOffered;
    }

    // Getters and Setters
    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getFacilityType() { return facilityType; }
    public void setFacilityType(String facilityType) { this.facilityType = facilityType; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getSpecialitiesOffered() { return specialitiesOffered; }
    public void setSpecialitiesOffered(String specialitiesOffered) { this.specialitiesOffered = specialitiesOffered; }

    /**
     * 获取专业列表
     */
    public List<String> getSpecialityList() {
        if (specialitiesOffered == null || specialitiesOffered.trim().isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.asList(specialitiesOffered.split("\\|"));
    }

    /**
     * 检查是否提供特定专业服务
     */
    public boolean offersSpeciality(String speciality) {
        List<String> specialities = getSpecialityList();
        return specialities.contains(speciality);
    }

    /**
     * 检查是否为医院
     */
    public boolean isHospital() {
        return "Hospital".equals(facilityType);
    }

    /**
     * 检查是否为GP诊所
     */
    public boolean isGPSurgery() {
        return "GP Surgery".equals(facilityType);
    }

    /**
     * 获取完整地址
     */
    public String getFullAddress() {
        return (address != null ? address : "") + ", " + (postcode != null ? postcode : "");
    }

    /**
     * 检查设施是否开放
     */
    public boolean isOpen24Hours() {
        return openingHours != null && openingHours.contains("24/7");
    }

    @Override
    public boolean isValid() {
        return id != null && !id.trim().isEmpty() &&
               facilityName != null && !facilityName.trim().isEmpty() &&
               facilityType != null && !facilityType.trim().isEmpty() &&
               address != null && !address.trim().isEmpty() &&
               postcode != null && !postcode.trim().isEmpty() &&
               capacity > 0;
    }

    @Override
    public String getEntityType() {
        return "Facility";
    }

    @Override
    public String toString() {
        return "Facility{" +
                "id='" + id + '\'' +
                ", name='" + facilityName + '\'' +
                ", type='" + facilityType + '\'' +
                ", capacity=" + capacity +
                ", specialities=" + getSpecialityList() +
                '}';
    }
}
