package com.inkneko.nekorecord.ui.component;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.inkneko.nekorecord.data.RecordRepository;
import com.inkneko.nekorecord.data.model.Record;
import com.inkneko.nekorecord.data.model.RecordTag;
import com.inkneko.nekorecord.data.model.relations.CategoryInfo;

import java.util.List;

public class RecordEditViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel

    private RecordRepository recordRepository;

    public RecordEditViewModel(Application application) {
        super(application);
        recordRepository = new RecordRepository(getApplication());
    }

    public LiveData<List<CategoryInfo>> getCategories(String typename){
        return recordRepository.getCategories(typename);
    }

    public LiveData<Long> createRecord(Record record){
        return recordRepository.createRecord(record);
    }

    public void updateRecord(Record record){
        recordRepository.updateRecord(record);
    }

    public void addRecordTag(RecordTag recordTag){
        recordRepository.addRecordTag(recordTag);
    }

    public void addRecordTags(RecordTag[] recordTags){
        recordRepository.addRecordTags(recordTags);
    }

    public LiveData<Boolean> clearRecordTags(Long recordId){
        return recordRepository.removeRecordTagsByRecordId(recordId);
    }

    public void removeRecord(Record record){
        recordRepository.removeRecord(record);
    }

}