package com.inkneko.nekorecord.data.model.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.inkneko.nekorecord.data.model.Category;
import com.inkneko.nekorecord.data.model.Record;
import com.inkneko.nekorecord.data.model.RecordTag;
import com.inkneko.nekorecord.data.model.Tag;

import java.util.List;

public class RecordInfo {
    @Embedded public Record recordDetail;
    @Relation(
            parentColumn = "record_id",
            entityColumn = "tag_id",
            associateBy = @Junction(RecordTag.class)
    )
    public List<Tag> recordTags;

    @Relation(
            parentColumn = "record_category_id",
            entityColumn = "category_id"
    )
    public Category category;
}
