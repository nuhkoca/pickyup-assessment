package com.upday.shutterdemo.pickyup.di.module;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.upday.shutterdemo.pickyup.db.PickyUpDatabase;
import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.model.local.dao.FavoriteImagesDao;
import com.upday.shutterdemo.pickyup.repository.db.FavoriteImagesRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {

    @Provides
    Migration provideMigration(){
        return new Migration(2,3) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                // do nothing
            }
        };
    }

    @Provides
    @Singleton
    PickyUpDatabase providePickyUpDatabase(Application application, Migration migration){
        return Room.databaseBuilder(application, PickyUpDatabase.class, Constants.PICKYUP_DATABASE_NAME).build();
    }

    @Provides
    @Singleton
    FavoriteImagesDao provideFavoriteImagesDao(PickyUpDatabase pickyUpDatabase){
        return pickyUpDatabase.favoriteImagesDao();
    }

    @Provides
    @Singleton
    FavoriteImagesRepository provideFavoriteImagesRepository(FavoriteImagesDao favoriteImagesDao){
        return new FavoriteImagesRepository(favoriteImagesDao);
    }
}
