package com.example.demo_clean_code.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo_clean_code.data.model.UserEntity
import com.example.demo_clean_code.domain.AddUserUseCase
import com.example.demo_clean_code.domain.ClearUsersUseCase
import com.example.demo_clean_code.domain.DeleteUserUseCase
import com.example.demo_clean_code.domain.GetUsersUseCase
import com.example.demo_clean_code.domain.NetworkStateResources
import com.example.demo_clean_code.domain.SearchUsersUseCase
import com.example.demo_clean_code.ui.model.SnackbarEffect
import com.example.demo_clean_code.ui.model.UserIntent
import com.example.demo_clean_code.ui.model.UserViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val clearUsersUseCase: ClearUsersUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
):ViewModel() {

    private val _viewState = MutableStateFlow(UserViewState())
    val viewState : StateFlow<UserViewState> = _viewState

    private val _effectChannel = Channel<SnackbarEffect>()
    val effectFlow : Flow<SnackbarEffect> = _effectChannel.receiveAsFlow()

    private var recentlyDeletedUser: UserEntity? = null

    private var searchJob : Job? = null


    private fun loadUsers(){
        viewModelScope.launch(Dispatchers.IO) {
            getUsersUseCase().collectLatest {
                resource->
                when(resource){
                    is NetworkStateResources.Loading -> withContext(Dispatchers.Main){
                        _viewState.update {
                            it.copy(isLoading = true)
                        }
                    }

                    is NetworkStateResources.Success -> withContext(Dispatchers.Main){
                        _viewState.update {
                            it.copy(
                                isLoading = false,
                                users = resource.data,
                                filteredUserList = resource.data,

                            )
                        }
                    }

                    is NetworkStateResources.Error -> withContext(Dispatchers.Main){
                        _viewState.update {
                            it.copy(
                                isLoading = false
                            )
                        }
                        _effectChannel.send(SnackbarEffect.ShowSnackbar("Error loading users: ${resource.message}"))
                    }
                }
            }
        }
    }




}