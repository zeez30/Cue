package com.zeez.nourishquest.data.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.zeez.nourishquest.data.dao.HungerLogDao;
import com.zeez.nourishquest.data.dao.JournalEntryDao;
import com.zeez.nourishquest.data.dao.MealEntryDao;
import com.zeez.nourishquest.data.entity.HungerLog;
import com.zeez.nourishquest.data.entity.JournalEntry;
import com.zeez.nourishquest.data.entity.MealEntry;

@Database(
        entities = {HungerLog.class, MealEntry.class, JournalEntry.class},
        version = 2,
        exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "cue.db";
    private static volatile AppDatabase instance;

    // Migration to handle database updates without losing user data
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE meal_entries ADD COLUMN meal_type TEXT"
            );
        }
    };

    // DAOs for database interaction
    public abstract HungerLogDao hungerLogDao();
    public abstract MealEntryDao mealEntryDao();
    public abstract JournalEntryDao journalEntryDao();

    // Singleton pattern to prevent multiple instances of the database opening
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME
                            )
                            .addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigrationOnDowngrade()
                            .build();
                }
            }
        }
        return instance;
    }
}