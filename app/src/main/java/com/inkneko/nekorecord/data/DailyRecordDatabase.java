package com.inkneko.nekorecord.data;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {DailyRecord.class}, version = 1, exportSchema = false)
public abstract class DailyRecordDatabase extends RoomDatabase {

    public abstract DailyRecordDao dailyRecordDao();

    private static volatile DailyRecordDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static DailyRecordDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DailyRecordDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DailyRecordDatabase.class, "daily_record")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Copy from the tutorial. I think this might be useful in sometimes, so I just keep it.
     */
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                // If you want to start with some records, add them here.
            });
        }
    };
}
