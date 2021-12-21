package com.inkneko.nekorecord.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "record_tag", primaryKeys = {"record_id", "tag_id"}, indices = {@Index("tag_id")})
public class RecordTag {

    @NonNull
    @ColumnInfo(name = "record_id")
    private Long recordId;

    @NonNull
    @ColumnInfo(name = "tag_id")
    private Long tagId;

    public RecordTag(Long recordId, Long tagId) {
        this.recordId = recordId;
        this.tagId = tagId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
