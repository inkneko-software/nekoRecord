package com.inkneko.nekorecord.data.localstorage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Transaction;
import androidx.room.Update;

import com.inkneko.nekorecord.data.model.Record;
import com.inkneko.nekorecord.data.model.RecordTag;
import com.inkneko.nekorecord.data.model.relations.RecordInfo;

import java.util.List;

@Dao
public interface RecordManagementDao {

    @Query("delete from record")
    public void clearRecords();

    @Query("delete from record_tag")
    public void clearRecordTags();

    @Insert
    public long createRecord(Record record);

    @Update
    public void updateRecord(Record record);

    @Delete
    public void removeRecord(Record record);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addRecordTag(RecordTag recordTag);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addRecordTags(RecordTag[] recordTags);

    @Delete
    public void removeRecordTag(RecordTag recordTag);

    @Query("DELETE FROM record_tag where record_id = :recordId")
    public void removeRecordTagsByRecordId(Long recordId);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Transaction
    @Query("select * from record  where record.add_date >= :start order by add_date desc")
    public LiveData<List<RecordInfo>> getRecordInfosAboveTime(Long start);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Transaction
    @Query("select * from record left join category  on record_category_id = category_id left join record_tag on record.record_id = record_tag.record_id where category_name = :recordType and add_date >= :start group by record.record_id order by add_date desc")
    public LiveData<List<RecordInfo>> getRecordInfosAboveTime(String recordType, Long start);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Transaction
    @Query("select * from record where add_date >= :start and add_date <= :end order by add_date desc")
    public LiveData<List<RecordInfo>> getRecordInfosByTimeRange(Long start, Long end);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Transaction
    @Query("select * from record left join category  on record_category_id = category_id left join record_tag on record.record_id = record_tag.record_id where category_name = :recordType and add_date >= :start and add_date <= :end order by add_date desc")
    public LiveData<List<RecordInfo>> getRecordInfosByTimeRange(String recordType, Long start, Long end);
}
