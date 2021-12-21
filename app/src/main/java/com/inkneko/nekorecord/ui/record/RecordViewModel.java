package com.inkneko.nekorecord.ui.record;

import android.app.Application;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.inkneko.nekorecord.data.RecordRepository;
import com.inkneko.nekorecord.data.model.Record;
import com.inkneko.nekorecord.data.model.relations.CategoryInfo;
import com.inkneko.nekorecord.data.model.relations.RecordInfo;

import java.util.Date;
import java.util.List;

public class RecordViewModel extends AndroidViewModel {

    //private LiveData<List<DailyRecord>> mMorningRecord;
    private RecordRepository recordRepository;
    public RecordViewModel(Application application) {
        super(application);
        recordRepository = new RecordRepository(application);
    }

    public LiveData<List<RecordInfo>> getRecordInfosToday(){
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        long start = date.getTimeInMillis();
        date.set(Calendar.HOUR_OF_DAY, date.getMaximum(Calendar.HOUR_OF_DAY));
        date.set(Calendar.MINUTE, date.getMaximum(Calendar.MINUTE));
        date.set(Calendar.SECOND, date.getMaximum(Calendar.SECOND));
        date.set(Calendar.MILLISECOND, date.getMaximum(Calendar.MILLISECOND));
        long end = date.getTimeInMillis();

        return recordRepository.getRecordInfosByTimeRange(start, end);
    }

    public LiveData<List<RecordInfo>> getRecordInfosByRange(Long start, Long end){
        return recordRepository.getRecordInfosByTimeRange(start, end);
    }

    /*
    public LiveData<List<DailyRecord>> getRecords(String eventType, int offset, int limit) {
        return mRepo.getRecords(eventType, offset, limit);
    }

    public LiveData<List<DailyRecord>> getRecordsToday(String eventType) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return mRepo.getRecordsAboveTime(eventType, date.getTimeInMillis());
    }

    public LiveData<List<DailyRecord>> getRecordsSomeday(String eventType, Long timestampInDay){
        Calendar date = new GregorianCalendar();
        date.setTimeInMillis(timestampInDay);

        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        Long start = date.getTimeInMillis();
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
        Long end = date.getTimeInMillis();

        return mRepo.getRecordsByRange(eventType, start, end);
    }

    public void addRecord(DailyRecord record){
        mRepo.insert(record);
    }

    public void removeRecord(DailyRecord record) {
        mRepo.delete(record);
    }*/
}