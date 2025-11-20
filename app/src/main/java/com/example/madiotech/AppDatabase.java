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
            // ///////////////////////////////////////////////////////////////////////////////
            // IMPORTANT:
            // The SQL below is a *placeholder*. You need to replace it with the actual
            // SQL statements that reflect the changes you made to your LoginResponse entity
            // or any other schema changes that caused the crash.
            //
            // If you don't know the exact change, here are common scenarios:
            //
            // SCENARIO A: You added a new column to LoginResponse
            //    Example: If you added a column named 'timestamp' of type LONG (INTEGER in SQLite)
            //    database.execSQL("ALTER TABLE LoginResponse ADD COLUMN timestamp INTEGER NOT NULL DEFAULT 0");
            //    (Note: If you add a NOT NULL column, you must provide a DEFAULT value)
            //
            // SCENARIO B: You changed the type of an existing column (e.g., String to Integer)
            //    This is more complex and often involves:
            //    1. Renaming the old table.
            //    2. Creating the new table with the updated schema.
            //    3. Copying data from the old table to the new table, applying necessary transformations.
            //    4. Dropping the old table.
            //
            // SCENARIO C: You removed a column. No SQL needed in migration, as Room handles this
            //    when you rebuild the table. However, if other changes combined with this
            //    are causing issues, consider a full table recreation (complex scenario B).
            //
            // SCENARIO D: The LoginResponse entity itself was changed (e.g., its @Entity name changed)
            //    or you previously had different entities and now only have LoginResponse.
            //    This might also require table recreation.
            //
            // Since you were using `fallbackToDestructiveMigration` before, and the crash points to
            // a general schema integrity issue, let's assume for now you added a simple column.
            // If you can't recall the specific change, a safe *initial* guess for a common
            // simple change is adding a column.
            //
            // For example, if you added a new field like 'accessTokenExpiry' to LoginResponse:
            //
            // If you have `exportSchema = true` (which we just set), after rebuilding your project,
            // check the `app/schemas/1.json` and `app/schemas/2.json` files to see the exact
            // differences. These JSON files will show the schema definition for each version.
            //
            // If you *really* don't know the change and `exportSchema` doesn't help yet,
            // as a temporary measure to just get the app running *with a migration*,
            // you could make a migration that recreates the table. This is essentially
            // a "destructive migration" but through a migration object, which is good
            // for learning the structure.
            // Let's assume the table name is `LoginResponse`.
            //
            // Example of a table recreation (if you can't pinpoint the column change)
            // This is safer than just guessing an ALTER TABLE if you don't know.
            // It will delete all data in the LoginResponse table.
            // If you need to preserve data, you HAVE to know the exact schema change.
            database.execSQL("DROP TABLE IF EXISTS `LoginResponse`");
            database.execSQL("CREATE TABLE IF NOT EXISTS `LoginResponse` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "`username` TEXT," +
                    "`email` TEXT," +
                    "`token` TEXT," +
                    // Add any new columns here that you added to your LoginResponse entity
                    // Example: If you added a 'refreshToken' field in LoginResponse.java
                    // "`refreshToken` TEXT," +
                    // Example: If you added an 'expiryDate' field
                    // "`expiryDate` INTEGER," +
                    "`success` INTEGER NOT NULL)"); // Ensure boolean maps to INTEGER
            // ///////////////////////////////////////////////////////////////////////////////
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