package com.example.demo_clean_code.ui.model

import com.example.demo_clean_code.data.model.UserEntity

data class UserViewState (
    val isLoading: Boolean = false,
    val users: List<UserEntity> = emptyList(),
    val filteredUserList: List<UserEntity> = emptyList(),
    val name: String = "",
    val email: String = "",
    val nameError: Boolean = false,
    val emailError: Boolean = false,
    val searchQuery: String = "",
    val isDarkTheme: Boolean=false,
    val recentlyDeletedUser: UserEntity? = null, // Use UserEntity instead of User
    val selectedImageUri: String? = null
)