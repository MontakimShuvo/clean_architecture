package com.example.demo_clean_code.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.demo_clean_code.data.dao.UserDao
import com.example.demo_clean_code.data.model.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun userDao():UserDao
}