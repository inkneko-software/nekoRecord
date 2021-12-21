package com.inkneko.nekorecord.data.localstorage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.inkneko.nekorecord.data.model.Category;
import com.inkneko.nekorecord.data.model.Tag;
import com.inkneko.nekorecord.data.model.relations.CategoryInfo;

import java.util.List;

@Dao
public interface CategoryManagementDao {

    @Insert
    public long createCategory(Category category);

    @Insert
    public long createTag(Tag tag);

    @Transaction
    @Query("select * from category where type = :type ")
    LiveData<List<CategoryInfo>> getCategoryInfosByType(String type);

    @Transaction
    @Query("select * from tag where parent_category_id = :parentCategoryId")
    LiveData<List<Tag>> getTags(Long parentCategoryId);

    @Query("delete from category")
    void clearCategorys();

    @Query("delete from tag")
    void clearTags();


}
