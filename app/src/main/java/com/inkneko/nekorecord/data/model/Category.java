package com.inkneko.nekorecord.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "category")
public class Category {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    private Long categoryId;

    @ColumnInfo(name = "category_name")
    private String name;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "icon_resource_name")
    private String iconResourceName;

    public Category(Long categoryId, String name, String type, String iconResourceName) {
        this.categoryId = categoryId;
        this.name = name;
        this.type = type;
        this.iconResourceName = iconResourceName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIconResourceName() {
        return iconResourceName;
    }

    public void setIconResourceName(String iconResourceName) {
        this.iconResourceName = iconResourceName;
    }
}
