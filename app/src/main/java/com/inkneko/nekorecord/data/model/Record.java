package com.inkneko.nekorecord.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "record")
public class Record {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id")
    private Long recordId;

    @ColumnInfo(name = "record_category_id")
    private Long categoryId;

    @ColumnInfo(name = "value")
    private Float value;

    @ColumnInfo(name = "add_date")
    private Long addDate;

    @ColumnInfo(name = "remark")
    private String remark;

    public Record(Long recordId, Long categoryId, Float value, Long addDate, String remark) {
        this.recordId = recordId;
        this.categoryId = categoryId;
        this.value = value;
        this.addDate = addDate;
        this.remark = remark;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Long getAddDate() {
        return addDate;
    }

    public void setAddDate(Long addDate) {
        this.addDate = addDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
