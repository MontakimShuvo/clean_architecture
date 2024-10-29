package com.example.demo_clean_code.domain

import com.example.demo_clean_code.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(): Flow<NetworkStateResources<List<UserEntity>>>
    suspend fun addUser(user:UserEntity)
    suspend fun deleteUser(user:UserEntity)
    suspend fun clearUsers()
    fun searchUsers(query: String):Flow<NetworkStateResources<List<UserEntity>>>
}