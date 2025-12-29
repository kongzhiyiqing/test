package com.healthcare.model;

/**
 * 基础实体类
 * 所有数据模型的基类
 *
 * @author Healthcare System
 * @version 1.0
 */
public abstract class Entity {

    protected String id;

    /**
     * 构造函数
     */
    public Entity() {
    }

    /**
     * 构造函数
     */
    public Entity(String id) {
        this.id = id;
    }

    /**
     * 获取实体ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置实体ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 验证实体数据的有效性
     * @return 如果数据有效返回true，否则返回false
     */
    public abstract boolean isValid();

    /**
     * 获取实体类型名称
     */
    public abstract String getEntityType();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entity entity = (Entity) obj;
        return id != null ? id.equals(entity.id) : entity.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getEntityType() + "{id='" + id + "'}";
    }
}
