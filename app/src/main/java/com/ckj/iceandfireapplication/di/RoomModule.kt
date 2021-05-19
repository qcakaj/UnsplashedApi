package com.ckj.iceandfireapplication.di

import android.content.Context
import androidx.room.Room
import com.ckj.iceandfireapplication.db.ApplicationDatabase
import com.ckj.iceandfireapplication.db.dao.ImageDao
import com.ckj.iceandfireapplication.db.dao.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideApplicationDb(@ApplicationContext context: Context): ApplicationDatabase {
        return Room.databaseBuilder(context,
        ApplicationDatabase::class.java,
        ApplicationDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideImageDao(applicationDb: ApplicationDatabase) : ImageDao {
        return applicationDb.imageDao()
    }

    @Singleton
    @Provides
    fun provideRemoteKeysDao(applicationDb: ApplicationDatabase) : RemoteKeysDao {
        return applicationDb.remoteKeysDao()
    }
}