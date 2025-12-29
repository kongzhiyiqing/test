package com.healthcare.repository;

import com.healthcare.model.Entity;
import com.healthcare.util.HealthcareException;

import java.util.List;
import java.util.Optional;

/**
 * 数据仓库接口
 * 定义数据访问的基本操作
 *
 * @param <T> 实体类型
 * @author Healthcare System
 * @version 1.0
 */
public interface DataRepository<T extends Entity> {

    /**
     * 保存实体
     */
    void save(T entity) throws HealthcareException;

    /**
     * 根据ID查找实体
     */
    Optional<T> findById(String id) throws HealthcareException;

    /**
     * 获取所有实体
     */
    List<T> findAll() throws HealthcareException;

    /**
     * 根据ID删除实体
     */
    void deleteById(String id) throws HealthcareException;

    /**
     * 检查实体是否存在
     */
    boolean existsById(String id) throws HealthcareException;

    /**
     * 获取实体数量
     */
    long count() throws HealthcareException;

    /**
     * 保存所有实体
     */
    void saveAll(List<T> entities) throws HealthcareException;

    /**
     * 删除所有实体
     */
    void deleteAll() throws HealthcareException;

    /**
     * 从数据源重新加载数据
     */
    void reload() throws HealthcareException;

    /**
     * 将数据保存到持久化存储
     */
    void flush() throws HealthcareException;
}
