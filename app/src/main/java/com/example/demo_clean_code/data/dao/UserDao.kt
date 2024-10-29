package com.example.demo_clean_code.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.demo_clean_code.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user:UserEntity)

    @Delete
    suspend fun deleteUser(user:UserEntity)

    @Query("DELETE FROM users")
    suspend fun clearUsers()

    @Query("SELECT * FROM users WHERE name LIKE '%' || :searchQuery || '%' OR email LIKE '%' || :searchQuery || '%'")
    fun searchUsers(searchQuery: String): Flow<List<UserEntity>>

}