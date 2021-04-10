package com.inkneko.nekorecord.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DailyRecordRepository {

    private DailyRecordDao mDailyRecordDao;
    private LiveData<List<DailyRecord>> mAllRecords;

    public DailyRecordRepository(Application application) {
        DailyRecordDatabase db = DailyRecordDatabase.getDatabase(application);
        mDailyRecordDao = db.dailyRecordDao();

    }

    public LiveData<List<DailyRecord>> getAllRecords(String eventType) {
        mAllRecords = mDailyRecordDao.selectAll(eventType);
        return mAllRecords;
    }

    public LiveData<List<DailyRecord>> getRecords(String eventType,int offset, int limit) {
        mAllRecords = mDailyRecordDao.select(eventType, offset, limit);
        return mAllRecords;
    }

    public LiveData<List<DailyRecord>> getRecordsAboveTime(String eventType, Long timestamp) {
        mAllRecords = mDailyRecordDao.select(eventType, timestamp);
        return mAllRecords;
    }

    public LiveData<List<DailyRecord>> getRecordsAboveTime( Long timestamp) {
        mAllRecords = mDailyRecordDao.selectAll(timestamp);
        return mAllRecords;
    }

    public LiveData<List<DailyRecord>> getRecordsByRange(Long start, Long end){
        return mDailyRecordDao.selectAll(start, end);
    }

    public LiveData<List<DailyRecord>> getRecordsByRange(String eventType,Long start, Long end){
        return mDailyRecordDao.select(eventType, start, end);
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(DailyRecord record) {
        DailyRecordDatabase.databaseWriteExecutor.execute(() -> {
            mDailyRecordDao.insert(record);
        });
    }

    public void delete(DailyRecord record){
        DailyRecordDatabase.databaseWriteExecutor.execute(() -> {
            mDailyRecordDao.delete(record);
        });
    }

    public  void backupToServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String requestData = new Gson().toJson(new RequestData(android.os.Build.MODEL, mDailyRecordDao.dump()));

                MediaType JSON = MediaType.get("application/json; charset=utf-8");

                OkHttpClient client = new OkHttpClient();

                    RequestBody body = RequestBody.create(JSON, requestData);
                    Request request = new Request.Builder()
                            .url("http://192.168.31.188:8080/api/v1/record/saveRecord")
                            .post(body)
                            .build();
                    try (Response response = client.newCall(request).execute()) {

                    }catch (IOException e){

                    }
            }
        }).start();
    }

    public void fetchFromServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String requestData = new Gson().toJson(new RequestData(android.os.Build.MODEL, mDailyRecordDao.dump()));

                MediaType JSON = MediaType.get("application/json; charset=utf-8");

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://192.168.31.188:8080/api/v1/record/fetchRecord")
                        .get()
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    FetchRecordDto fetchRecordDto = new Gson().fromJson(response.body().string(), FetchRecordDto.class);
                    //mDailyRecordDao.removeAll();

                    for(int i = 0; i <  fetchRecordDto.getRecordList().length; ++i){
                        mDailyRecordDao.insert(fetchRecordDto.getRecordList()[i]);
                    }
                }catch (IOException e){

                }
            }
        }).start();
    }
}

class RequestData{
    public RequestData(String device, List<DailyRecord> recordList) {
        this.device = device;
        this.recordList = recordList;
    }

    public String device;
    public List<DailyRecord> recordList;
}

class FetchRecordDto{
    public FetchRecordDto(DailyRecord[] recordList) {
        this.recordList = recordList;
    }

    public DailyRecord[] getRecordList() {
        return recordList;
    }

    public void setRecordList(DailyRecord[] recordList) {
        this.recordList = recordList;
    }

    public DailyRecord[] recordList;

}