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
import com.example.demo_clean_code.isValidName
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

    private fun searchUsers(query:String){
        _viewState.update {
            it.copy(searchQuery = query)
        }
        if(query.isEmpty()){
            _viewState.update {
                it.copy(
                    filteredUserList = it.users,
                    isLoading = false
                )
            }
        }else{
            searchJob?.cancel()
            searchJob = viewModelScope.launch(Dispatchers.IO){
                searchUsersUseCase(query).collectLatest {
                    resource->
                    when(resource){
                        is NetworkStateResources.Loading -> withContext(Dispatchers.Main){
                            _viewState.update {
                                it.copy(
                                    isLoading = true
                                )
                            }
                        }

                        is NetworkStateResources.Success -> withContext(Dispatchers.Main){
                            _viewState.update {
                                it.copy(
                                    isLoading = false,
                                    filteredUserList = resource.data
                                )
                            }
                        }

                        is NetworkStateResources.Error -> withContext(Dispatchers.Main) {
                            _viewState.update { it.copy(isLoading = false) }
                            _effectChannel.send(SnackbarEffect.ShowSnackbar("Error searching users: ${resource.message}"))
                        }
                    }
                }
            }
        }
    }

    private fun deleteUser(user:UserEntity){
        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            recentlyDeletedUser = user
            try {
                deleteUserUseCase(user)
                withContext(Dispatchers.Main){
                    _viewState.update { it.copy(isLoading = false) }
                    _effectChannel.send(SnackbarEffect.ShowSnackbar("User deleted", "Undo"))
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    _viewState.update { it.copy(isLoading = false) }
                    _effectChannel.send(SnackbarEffect.ShowSnackbar("Error deleting user: ${e.message}"))
                }
            }
        }
    }

    private fun clearUsers() {
        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                clearUsersUseCase()
                withContext(Dispatchers.Main) {
                    _viewState.update { it.copy(isLoading = false) }
                    _effectChannel.send(SnackbarEffect.ShowSnackbar("All users cleared!"))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _viewState.update { it.copy(isLoading = false) }
                    _effectChannel.send(SnackbarEffect.ShowSnackbar("Error clearing users: ${e.message}"))
                }
            }
        }
    }

    private fun undoDelete() {
        recentlyDeletedUser?.let { deletedUser ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    addUserUseCase(deletedUser)
                    withContext(Dispatchers.Main) {
                        _effectChannel.send(SnackbarEffect.ShowSnackbar("User restored successfully!"))
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _effectChannel.send(SnackbarEffect.ShowSnackbar("Error restoring user: ${e.message}"))
                    }
                }
            }
        }
    }

    private fun validateAndAddUser(name: String, email: String, imagePath: String?){
        val nameError = !name.isValidName()
        val emailError = !email.isValidName()
        val imageError = !imagePath?.isValidName()!!

        if(nameError || emailError || imageError){
            val errorMessage = buildErrorMessage(nameError,emailError,imageError)
            _viewState.update { it.copy(nameError = nameError, emailError = emailError) }
            viewModelScope.launch(Dispatchers.Main) {
                _effectChannel.send(SnackbarEffect.ShowSnackbar(errorMessage))
            }
            return
        }

        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO){
            try {
                val imageUrl = imagePath
                addUserUseCase(UserEntity(name=name, email = email, imageUrl = imageUrl))
                withContext(Dispatchers.Main){
                    _viewState.update {
                        it.copy(
                            isLoading = false,
                            name="",
                            email = "",
                            nameError = false,
                            emailError = false
                        )
                    }
                    _effectChannel.send(SnackbarEffect.ShowSnackbar("User added successfully!"))
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    _viewState.update { it.copy(isLoading = false) }
                    _effectChannel.send(SnackbarEffect.ShowSnackbar("Error adding user: ${e.message}"))
                }
            }
        }
    }

    private fun validateName(name: String) {
        val nameError = !name.isValidName()
        _viewState.update { it.copy(name = name, nameError = nameError) }
    }

    private fun validateEmail(email: String) {
        val emailError = !email.isValidName()
        _viewState.update { it.copy(email = email, emailError = emailError) }
    }


    private fun buildErrorMessage(
        nameError: Boolean,
        emailError: Boolean,
        imageError: Boolean
    ): String {
        return when {
            nameError && emailError && imageError -> "Name, email, and image are required."
            nameError && emailError -> "Name and email are required."
            nameError -> "Name is required and must have at least 3 characters."
            emailError -> "Invalid email address."
            imageError -> "Please select or capture an image."
            else -> ""
        }
    }




}