package com.upday.shutterdemo.pickyup.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.model.local.dao.FavoriteImagesDao;
import com.upday.shutterdemo.pickyup.model.local.entity.FavoriteImages;

@Database(entities = {FavoriteImages.class}, version = 2, exportSchema = false)
public abstract class PickyUpDatabase extends RoomDatabase {

    private static PickyUpDatabase INSTANCE;

    public abstract FavoriteImagesDao favoriteImagesDao();

    public static PickyUpDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PickyUpDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    PickyUpDatabase.class, Constants.PICKYUP_DATABASE_NAME)
                                    .addMigrations(MIGRATION_2_3)
                                    //.fallbackToDestructiveMigration()
                                    .build();
                }
            }
        }

        return INSTANCE;
    }

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //Since we won't alter the table, there's nothing else to do here.
        }
    };

    public static void destroyInstance() {
        INSTANCE = null;
    }
}