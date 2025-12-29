package com.healthcare.repository;

import com.healthcare.model.Entity;
import com.healthcare.util.HealthcareException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CSV数据仓库抽象基类
 * 提供CSV文件的基本操作实现
 *
 * @param <T> 实体类型
 * @author Healthcare System
 * @version 1.0
 */
public abstract class CsvDataRepository<T extends Entity> implements DataRepository<T> {

    protected final String filePath;
    protected final List<T> cache;
    protected boolean loaded;

    /**
     * 构造函数
     */
    protected CsvDataRepository(String filePath) {
        this.filePath = filePath;
        this.cache = new ArrayList<>();
        this.loaded = false;
    }

    /**
     * 获取CSV文件头行
     */
    protected abstract String getCsvHeader();

    /**
     * 将实体转换为CSV行
     */
    protected abstract String entityToCsvRow(T entity);

    /**
     * 将CSV行解析为实体
     */
    protected abstract T csvRowToEntity(String csvRow) throws HealthcareException;

    /**
     * 确保数据已加载
     */
    protected void ensureLoaded() throws HealthcareException {
        if (!loaded) {
            loadFromFile();
            loaded = true;
        }
    }

    /**
     * 从文件加载数据
     */
    protected void loadFromFile() throws HealthcareException {
        cache.clear();

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            // 文件不存在，创建空文件
            try {
                Files.createDirectories(path.getParent());
                try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                    writer.println(getCsvHeader());
                }
            } catch (IOException e) {
                throw HealthcareException.dataLoadError("CSV文件", e);
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    // 跳过表头
                    isFirstLine = false;
                    continue;
                }

                if (!line.trim().isEmpty()) {
                    try {
                        T entity = csvRowToEntity(line);
                        if (entity != null && entity.isValid()) {
                            cache.add(entity);
                        }
                    } catch (Exception e) {
                        System.err.println("解析CSV行失败: " + line + ", 错误: " + e.getMessage());
                    }
                }
            }

            System.out.println("从 " + filePath + " 加载了 " + cache.size() + " 条记录");

        } catch (IOException e) {
            throw HealthcareException.dataLoadError("CSV文件", e);
        }
    }

    /**
     * 保存数据到文件
     */
    protected void saveToFile() throws HealthcareException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // 写入表头
            writer.println(getCsvHeader());

            // 写入数据行
            for (T entity : cache) {
                writer.println(entityToCsvRow(entity));
            }

            System.out.println("保存了 " + cache.size() + " 条记录到 " + filePath);

        } catch (IOException e) {
            throw HealthcareException.dataSaveError("CSV文件", e);
        }
    }

    @Override
    public void save(T entity) throws HealthcareException {
        if (entity == null) {
            throw HealthcareException.validationError("entity", "实体不能为空");
        }

        ensureLoaded();

        // 检查是否已存在
        Optional<T> existing = cache.stream()
                .filter(e -> entity.getId().equals(e.getId()))
                .findFirst();

        if (existing.isPresent()) {
            // 更新现有实体
            int index = cache.indexOf(existing.get());
            cache.set(index, entity);
        } else {
            // 添加新实体
            cache.add(entity);
        }

        // 保存到文件
        saveToFile();
    }

    @Override
    public Optional<T> findById(String id) throws HealthcareException {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }

        ensureLoaded();

        return cache.stream()
                .filter(entity -> id.equals(entity.getId()))
                .findFirst();
    }

    @Override
    public List<T> findAll() throws HealthcareException {
        ensureLoaded();
        return new ArrayList<>(cache);
    }

    @Override
    public void deleteById(String id) throws HealthcareException {
        if (id == null || id.trim().isEmpty()) {
            throw HealthcareException.validationError("id", "ID不能为空");
        }

        ensureLoaded();

        boolean removed = cache.removeIf(entity -> id.equals(entity.getId()));
        if (!removed) {
            throw HealthcareException.businessLogicError("deleteById", "实体不存在: " + id);
        }

        // 保存到文件
        saveToFile();
    }

    @Override
    public boolean existsById(String id) throws HealthcareException {
        return findById(id).isPresent();
    }

    @Override
    public long count() throws HealthcareException {
        ensureLoaded();
        return cache.size();
    }

    @Override
    public void saveAll(List<T> entities) throws HealthcareException {
        if (entities == null) {
            throw HealthcareException.validationError("entities", "实体列表不能为空");
        }

        ensureLoaded();

        // 清除现有数据
        cache.clear();

        // 添加新数据
        for (T entity : entities) {
            if (entity != null && entity.isValid()) {
                cache.add(entity);
            }
        }

        // 保存到文件
        saveToFile();
    }

    @Override
    public void deleteAll() throws HealthcareException {
        cache.clear();
        saveToFile();
    }

    @Override
    public void reload() throws HealthcareException {
        loaded = false;
        ensureLoaded();
    }

    @Override
    public void flush() throws HealthcareException {
        saveToFile();
    }

    /**
     * 获取文件路径
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 获取缓存中的实体数量
     */
    public int getCacheSize() {
        return cache.size();
    }

    /**
     * 检查数据是否已加载
     */
    public boolean isLoaded() {
        return loaded;
    }
}
