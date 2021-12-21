package com.inkneko.nekorecord.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.inkneko.nekorecord.data.localstorage.RecordDatabase;
import com.inkneko.nekorecord.data.localstorage.dao.CategoryManagementDao;
import com.inkneko.nekorecord.data.localstorage.dao.RecordManagementDao;
import com.inkneko.nekorecord.data.model.Category;
import com.inkneko.nekorecord.data.model.Record;
import com.inkneko.nekorecord.data.model.RecordTag;
import com.inkneko.nekorecord.data.model.Tag;
import com.inkneko.nekorecord.data.model.relations.CategoryInfo;
import com.inkneko.nekorecord.data.model.relations.RecordInfo;

import java.util.List;

/**
 * 账本记录仓库，封装底层实际的数据存储，提供数据的存取能力。
 */
public class RecordRepository {
    /**
     * what we need:
     * 创建类别
     * 修改类别名称
     * 删除类别
     * 在某个类别下创建标签
     * 查询某个类别下的标签
     * 创建收入记录
     * 修改收入记录
     * 创建支出记录
     * 修改支出记录
     */

    private RecordDatabase recordDatabase;
    private CategoryManagementDao categoryManagementDao;
    private RecordManagementDao recordManagementDao;
    private Application owner;

    public RecordRepository(Application application) {
        recordDatabase =  RecordDatabase.getDatabase(application);
        categoryManagementDao = recordDatabase.categoryManagementDao();
        recordManagementDao = recordDatabase.recordManagementDao();
        owner = application;
    }

    /**
     * 初始化本地数据库
     */
    public void initializeLocalStroage(){
        //清空数据库
        categoryManagementDao.clearTags();
        categoryManagementDao.clearCategorys();
        recordManagementDao.clearRecordTags();
        recordManagementDao.clearRecords();
        //添加数据库

        long foodId = categoryManagementDao.createCategory(new Category(null, "饮食", "支出", "ic_rice"));
        categoryManagementDao.createTag(new Tag(null, "早餐", foodId));
        categoryManagementDao.createTag(new Tag(null, "午餐", foodId));
        categoryManagementDao.createTag(new Tag(null, "晚餐", foodId));
        categoryManagementDao.createTag(new Tag(null, "零食", foodId));
        categoryManagementDao.createTag(new Tag(null, "饮料", foodId));
        categoryManagementDao.createTag(new Tag(null, "水果", foodId));
        categoryManagementDao.createTag(new Tag(null, "外卖", foodId));
        categoryManagementDao.createTag(new Tag(null, "夜宵", foodId));

        long travelId = categoryManagementDao.createCategory(new Category(null, "交通", "支出", "ic_subway"));
        categoryManagementDao.createTag(new Tag(null, "打车", travelId));
        categoryManagementDao.createTag(new Tag(null, "公交", travelId));
        categoryManagementDao.createTag(new Tag(null, "地铁", travelId));
        categoryManagementDao.createTag(new Tag(null, "停车费", travelId));
        categoryManagementDao.createTag(new Tag(null, "加油费", travelId));
        categoryManagementDao.createTag(new Tag(null, "火车票", travelId));
        categoryManagementDao.createTag(new Tag(null, "飞机票", travelId));

        categoryManagementDao.createCategory(new Category(null, "买菜", "支出", "ic_carrot"));
        categoryManagementDao.createCategory(new Category(null, "服饰", "支出", "ic_t_shirt"));
        categoryManagementDao.createCategory(new Category(null, "化妆品", "支出", "ic_cosmetic"));
        long daliyConsumeId = categoryManagementDao.createCategory(new Category(null, "日用品", "支出", "ic_tissue"));
        categoryManagementDao.createTag(new Tag(null, "超市", daliyConsumeId));
        categoryManagementDao.createTag(new Tag(null, "纸巾", daliyConsumeId));
        categoryManagementDao.createTag(new Tag(null, "洗漱用品", daliyConsumeId));

        categoryManagementDao.createCategory(new Category(null, "红包", "支出", "ic_red_envelope"));
        categoryManagementDao.createCategory(new Category(null, "话费", "支出", "ic_phone"));
        long recreationId = categoryManagementDao.createCategory(new Category(null, "娱乐", "支出", "ic_controller"));
        categoryManagementDao.createTag(new Tag(null, "电影", recreationId));
        categoryManagementDao.createTag(new Tag(null, "游戏", recreationId));
        categoryManagementDao.createTag(new Tag(null, "会员", recreationId));
        categoryManagementDao.createTag(new Tag(null, "门票", recreationId));
        categoryManagementDao.createTag(new Tag(null, "旅游", recreationId));

        categoryManagementDao.createCategory(new Category(null, "医疗", "支出", "ic_medicine"));
        long salaryId = categoryManagementDao.createCategory(new Category(null, "工资", "收入", "ic_money_bag"));
        categoryManagementDao.createTag(new Tag(null, "月工资", salaryId));
        categoryManagementDao.createTag(new Tag(null, "补贴", salaryId));
        categoryManagementDao.createTag(new Tag(null, "生活费", salaryId));

        categoryManagementDao.createCategory(new Category(null, "投资", "收入", "ic_investment"));
        categoryManagementDao.createCategory(new Category(null, "奖金", "收入", "ic_money_1"));
        categoryManagementDao.createCategory(new Category(null, "兼职", "收入", "ic_money_2"));
        categoryManagementDao.createCategory(new Category(null, "红包", "收入", "ic_red_envelope"));


    }

    public LiveData<Long> createCategory(String categoryName, String typeName, String iconResourceName){
        MutableLiveData<Long> callback = new MutableLiveData<>();
        recordDatabase.getQueryExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Category category = new Category(null, categoryName, typeName, iconResourceName);
                long categoryId = categoryManagementDao.createCategory(category);
                callback.postValue(categoryId);
            }
        });
        return callback;
    }

    public LiveData<Long> createTag(long categoryId, String name){
        MutableLiveData<Long> callback = new MutableLiveData<>();
        recordDatabase.getQueryExecutor().execute(new Runnable() {
            @Override
            public void run() {
                long tagId = categoryManagementDao.createTag(new Tag(null, name, categoryId));
                callback.postValue(tagId);
            }
        });
        return callback;
    }

    public LiveData<List<CategoryInfo>> getCategories(String type){
        return categoryManagementDao.getCategoryInfosByType(type);
    }

    public LiveData<List<RecordInfo>> getRecordInfosAboveTime(Long start){
        return recordManagementDao.getRecordInfosAboveTime(start);
    }

    public LiveData<List<RecordInfo>> getRecordInfosAboveTime(String type, Long start){
        return recordManagementDao.getRecordInfosAboveTime(type, start);
    }

    public LiveData<List<RecordInfo>> getRecordInfosByTimeRange(Long start, Long end){
        return recordManagementDao.getRecordInfosByTimeRange(start, end);
    }

    public LiveData<List<RecordInfo>> getRecordInfosByTimeRange(String type, Long start, Long end){
        return recordManagementDao.getRecordInfosByTimeRange(type, start, end);
    }

    public LiveData<Long> createRecord(Record record){
        MutableLiveData<Long> callback = new MutableLiveData<>();
        recordDatabase.getQueryExecutor().execute(() -> {
            long categoryId = recordManagementDao.createRecord(record);
            callback.postValue(categoryId);
        });
        return callback;
    }

    public void updateRecord(Record record){
        recordDatabase.getQueryExecutor().execute(() -> recordManagementDao.updateRecord(record));
    }

    public void removeRecord(Record record){
        recordDatabase.getQueryExecutor().execute(()->{
            recordManagementDao.removeRecord(record);
        });
    }

    public void addRecordTag(RecordTag recordTag){
        recordDatabase.getQueryExecutor().execute(() -> recordManagementDao.addRecordTag(recordTag));
    }

    public void addRecordTags(RecordTag[] recordTags){
        recordDatabase.getQueryExecutor().execute(() -> recordManagementDao.addRecordTags(recordTags));
    }

    public LiveData<Boolean> removeRecordTagsByRecordId(Long recordId){
        MutableLiveData<Boolean> callback = new MutableLiveData<>();
        recordDatabase.getQueryExecutor().execute(() -> {
            recordManagementDao.removeRecordTagsByRecordId(recordId);
            callback.postValue(true);
        });
        return callback;
    }

}

