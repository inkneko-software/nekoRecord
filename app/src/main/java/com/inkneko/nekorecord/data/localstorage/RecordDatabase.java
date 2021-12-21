package com.inkneko.nekorecord.data.localstorage;
import android.content.Context;
import android.os.FileUtils;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.util.FileUtil;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.inkneko.nekorecord.data.localstorage.dao.CategoryManagementDao;
import com.inkneko.nekorecord.data.localstorage.dao.RecordManagementDao;
import com.inkneko.nekorecord.data.model.Category;
import com.inkneko.nekorecord.data.model.Record;
import com.inkneko.nekorecord.data.model.RecordTag;
import com.inkneko.nekorecord.data.model.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 实现账本记录的本地存储。
 */
@Database(entities = {Category.class, Tag.class, Record.class, RecordTag.class}, version = 1, exportSchema = false)
public abstract class RecordDatabase extends RoomDatabase {

    public abstract CategoryManagementDao categoryManagementDao();
    public abstract RecordManagementDao recordManagementDao();
    public static final String databaseName = "nekoRecord";

    private static volatile RecordDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static RecordDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecordDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RecordDatabase.class, databaseName)
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public static void restoreDatabase(final Context context, FileInputStream dbInputStream){
        synchronized (RecordDatabase.class){
            if (INSTANCE != null){
                INSTANCE.close();
                File tempFile = new File(context.getFilesDir(), "temp.db");
                if (tempFile.exists()){
                    tempFile.delete();
                }

                try{
                    boolean jb = tempFile.createNewFile();
                    FileOutputStream tempFileOutputStream = new FileOutputStream(tempFile);
                    int readNum = 0;
                    byte[] buffer = new byte[65535];
                    while((readNum = dbInputStream.read(buffer, 0, 65535)) != -1){
                        tempFileOutputStream.write(buffer, 0, readNum);
                    }
                    tempFileOutputStream.flush();
                    tempFileOutputStream.close();
                }catch (IOException ignored){}

                File oldDatabaseFile = context.getDatabasePath(databaseName);
                oldDatabaseFile.delete();
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        RecordDatabase.class, databaseName)
                        .createFromFile(tempFile)
                        .build();

            }
        }
    }
    /**
     * Copy from the tutorial. I think this might be useful in sometimes, so I just keep it.
     *//*
    private static RoomDatabase.Callback onDatabaseBuilt = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            File tempFile = new File(.getFilesDir(), "temp.db");
            tempFile.delete();
        }
    };*/
}
