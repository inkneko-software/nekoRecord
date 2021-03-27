package com.inkneko.nekorecord.ui.history;

import android.app.Application;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.inkneko.nekorecord.data.DailyRecord;
import com.inkneko.nekorecord.data.DailyRecordRepository;

import java.util.Calendar;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private DailyRecordRepository mRepo;

    public HistoryViewModel(Application application) {
        super(application);
        mRepo = new DailyRecordRepository(application);

    }


    public LiveData<List<DailyRecord>> fetchThisMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return mRepo.getRecordsAboveTime(calendar.getTimeInMillis());
    }
}