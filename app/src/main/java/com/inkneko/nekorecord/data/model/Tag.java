package com.inkneko.nekorecord.data.model;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "tag"/*, foreignKeys = {
        @ForeignKey(
            entity = Category.class,
            parentColumns = "category_id",
            childColumns = "parent_category_id"
        )
}*/)
public class Tag {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tag_id")
    private Long tagId;

    @ColumnInfo(name = "tag_name")
    private String name;

    @ColumnInfo(name = "parent_category_id")
    private Long categoryId;

    public Tag(Long tagId, String name, Long categoryId) {
        this.tagId = tagId;
        this.name = name;
        this.categoryId = categoryId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @NotNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if (obj instanceof Tag && ((Tag) obj).tagId.equals(this.tagId)){
            return true;
        }
        return false;
    }
}
