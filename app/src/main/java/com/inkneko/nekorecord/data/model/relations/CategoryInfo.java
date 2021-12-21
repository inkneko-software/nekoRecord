package com.inkneko.nekorecord.data.model.relations;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.inkneko.nekorecord.data.model.Category;
import com.inkneko.nekorecord.data.model.Tag;

import java.util.List;

public class CategoryInfo {
    @Embedded public Category category;
    @Relation(
        parentColumn = "category_id",
        entityColumn = "parent_category_id"
    )
    public List<Tag> tags;
}
