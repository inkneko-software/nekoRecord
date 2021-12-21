package com.inkneko.nekorecord.ui.history;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.inkneko.nekorecord.data.RecordRepository;
import com.inkneko.nekorecord.data.model.relations.RecordInfo;

import java.util.Calendar;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private RecordRepository recordRepository;

    public HistoryViewModel(Application application) {
        super(application);
        recordRepository = new RecordRepository(application);

    }


    public LiveData<List<RecordInfo>> fetchThisMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long start = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));
        return recordRepository.getRecordInfosByTimeRange(start, calendar.getTimeInMillis());
    }

    public LiveData<List<RecordInfo>> fetchRange(Long start, Long end){
        return recordRepository.getRecordInfosByTimeRange(start, end);
    }
/*
    public LiveData<List<DailyRecord>> getHistorySomeday(Long timestampInDay){
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

        return mRepo.getRecordsByRange(start, end);
    }

    public void saveRecord(DailyRecord record){
        mRepo.insert(record);
    }

    public void removeRecord(DailyRecord record){
        mRepo.delete(record);
    }

*/

}