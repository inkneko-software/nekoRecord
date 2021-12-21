package com.inkneko.nekorecord.data.remotestorage;


/**
 * 实现账本记录的远端存取，技术原因不进行实现。
 */
public class RemoteStorageExample {
}

class RequestData{
    /*
    public RequestData(String device, List<DailyRecord> recordList) {
        this.device = device;
        this.recordList = recordList;
    }

    public String device;
    public List<DailyRecord> recordList;

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
    }*/
}
/*
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

}*/