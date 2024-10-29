package com.example.demo_clean_code.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val clearUsersUseCase: ClearUsersUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val saveThemeUseCase: SaveThemeUseCase,
    private val getThemeUseCase: GetThemeUseCase
):ViewModel() {

    private val _

}