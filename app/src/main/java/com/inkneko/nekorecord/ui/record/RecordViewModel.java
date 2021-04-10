package com.inkneko.nekorecord.ui.record;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.inkneko.nekorecord.data.DailyRecord;
import com.inkneko.nekorecord.data.DailyRecordRepository;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class RecordViewModel extends AndroidViewModel {

    //private LiveData<List<DailyRecord>> mMorningRecord;
    private DailyRecordRepository mRepo;
    public RecordViewModel(Application application) {
        super(application);
        mRepo = new DailyRecordRepository(application);
    }

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
    }
}