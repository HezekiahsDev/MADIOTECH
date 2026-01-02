package com.example.madiotech;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration; // <--- NEW IMPORT
import androidx.sqlite.db.SupportSQLiteDatabase; // <--- NEW IMPORT

import com.example.madiotech.api.LoginResponse;

// STEP 1: Update the @Database annotation
// - Increment the version from 1 to 2
// - Set exportSchema to true (recommended for migrations, Room will generate schema files)
@Database(entities = {LoginResponse.class}, version = 2, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract UserDao userDao();

    // STEP 2: Add your Migration object
    // This defines how to migrate from version 1 to version 2.
    // YOU MUST CUSTOMIZE THE SQL INSIDE migrate() BASED ON YOUR ACTUAL SCHEMA CHANGE.
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Migration from version 1 -> 2
            // We added new nullable text columns notice1..notice5 to the `user` table (LoginResponse entity).
            // Use ALTER TABLE to add them safely without dropping existing data.
            try {
                database.execSQL("ALTER TABLE `user` ADD COLUMN `notice1` TEXT");
            } catch (Exception ignored) {}
            try {
                database.execSQL("ALTER TABLE `user` ADD COLUMN `notice2` TEXT");
            } catch (Exception ignored) {}
            try {
                database.execSQL("ALTER TABLE `user` ADD COLUMN `notice3` TEXT");
            } catch (Exception ignored) {}
            try {
                database.execSQL("ALTER TABLE `user` ADD COLUMN `notice4` TEXT");
            } catch (Exception ignored) {}
            try {
                database.execSQL("ALTER TABLE `user` ADD COLUMN `notice5` TEXT");
            } catch (Exception ignored) {}
        }
    };

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "user_db")
                    // STEP 3: Remove fallbackToDestructiveMigration()
                    // Add your migration strategy here
                    .addMigrations(MIGRATION_1_2) // <--- ADD THIS LINE
                    .build();
        }
        return instance;
    }
}