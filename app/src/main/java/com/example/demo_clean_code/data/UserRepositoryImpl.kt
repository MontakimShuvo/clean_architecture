package com.example.demo_clean_code.data

import com.example.demo_clean_code.data.dao.UserDao
import com.example.demo_clean_code.data.model.UserEntity
import com.example.demo_clean_code.domain.NetworkStateResources
import com.example.demo_clean_code.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userDao: UserDao): UserRepository {
    override fun getUsers(): Flow<NetworkStateResources<List<UserEntity>>>  = flow {
        emit(NetworkStateResources.Loading)
        userDao.getAllUsers().collect{ users->
            emit(NetworkStateResources.Success(users))

        }
    }.catch { e->
        emit(NetworkStateResources.Error(e.localizedMessage ?: "Unknown error occurred"))
    }

    override suspend fun addUser(user: UserEntity) {
        try {
            userDao.insertUser(user)
        }catch (e: Exception){
            throw e
        }
    }

    override suspend fun deleteUser(user: UserEntity) {
        try {
            userDao.deleteUser(user)
        }catch (e: Exception){
            throw e
        }
    }

    override suspend fun clearUsers() {
        try {
            userDao.clearUsers()
        }catch (e: Exception){
            throw e
        }
    }

    override fun searchUsers(query: String): Flow<NetworkStateResources<List<UserEntity>>> = flow {
        emit(NetworkStateResources.Loading)
        userDao.searchUsers(query).collect { users ->
            emit(NetworkStateResources.Success(users))
        }
    }.catch { e ->
        emit(NetworkStateResources.Error(e.localizedMessage ?: "Unknown error occurred"))
    }

}