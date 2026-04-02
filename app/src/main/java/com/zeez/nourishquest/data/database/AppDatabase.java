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

// Room database — single instance shared across the whole app.
// All data stays on-device, no cloud sync.
//
// Version history:
//   v1 — initial schema
//   v2 — added meal_type column to meal_entries
@Database(
    entities = {HungerLog.class, MealEntry.class, JournalEntry.class},
    version = 2,
    exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "cue.db";
    private static volatile AppDatabase instance;

    // v1 → v2: adds meal_type as nullable TEXT so existing rows aren't affected
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                "ALTER TABLE meal_entries ADD COLUMN meal_type TEXT"
            );
        }
    };

    public abstract HungerLogDao hungerLogDao();
    public abstract MealEntryDao mealEntryDao();
    public abstract JournalEntryDao journalEntryDao();

    // Double-checked locking singleton — safe because Room handles thread-safe DB access internally
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
                    .build();
                }
            }
        }
        return instance;
    }
}
