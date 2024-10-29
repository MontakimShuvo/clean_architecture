package com.example.demo_clean_code.data.database

import android.content.Context
import androidx.room.Room
import com.example.demo_clean_code.data.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "user_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: AppDatabase):UserDao{
        return db.userDao()
    }
}