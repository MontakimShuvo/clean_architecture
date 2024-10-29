package com.example.demo_clean_code.domain

import com.example.demo_clean_code.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    operator fun invoke(): Flow<NetworkStateResources<List<UserEntity>>>{
        return userRepository.getUsers()
    }
}

class AddUserUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(user: UserEntity) {
        userRepository.addUser(user)
    }
}

class ClearUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        // Clear all users from the repository
        userRepository.clearUsers()
    }
}


class DeleteUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserEntity) {
        userRepository.deleteUser(user)
    }
}


class SearchUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(query: String): Flow<NetworkStateResources<List<UserEntity>>> {
        return userRepository.searchUsers(query)
    }
}