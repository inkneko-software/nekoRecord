package com.inkneko.nekorecord.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DailyRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DailyRecord dailyRecord);

    @Delete
    void delete(DailyRecord dailyRecord);

    @Query("DELETE FROM daily_record")
    void removeAll();

    /**
     * select records by its type, split by limit
     * @param eventType
     * @param offset
     * @param range
     * @return
     */
    @Query("SELECT * FROM daily_record WHERE event_type = :eventType ORDER BY timestamp DESC LIMIT :offset, :range")
    LiveData<List<DailyRecord>> select(String eventType, int offset, int range);

    /**
     * select the type of the records that above the given timestamp;
     * @param eventType
     * @param timestamp
     * @return
     */
    @Query("SELECT * FROM daily_record WHERE event_type = :eventType AND timestamp > :timestamp ORDER BY timestamp DESC")
    LiveData<List<DailyRecord>> select(String eventType, Long timestamp);

    /**
     * select all the records that above the given timestamp
     * @param timestamp
     * @return
     */
    @Query("SELECT * FROM daily_record WHERE timestamp > :timestamp ORDER BY timestamp DESC")
    LiveData<List<DailyRecord>> selectAll(Long timestamp);

    /**
     * select all the records that above the given timestamp
     * @param start begin time
     * @param  end end time
     * @return
     */
    @Query("SELECT * FROM daily_record WHERE timestamp > :start and timestamp < :end ORDER BY timestamp DESC")
    LiveData<List<DailyRecord>> selectAll(Long start, Long end);

    @Query("SELECT * FROM daily_record WHERE event_type = :eventType")
    LiveData<List<DailyRecord>> selectAll(String eventType);

    @Query("SELECT * FROM daily_record")
    List<DailyRecord> dump();

}
