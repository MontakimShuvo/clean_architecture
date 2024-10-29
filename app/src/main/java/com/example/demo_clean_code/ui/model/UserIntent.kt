package com.example.demo_clean_code.ui.model

import com.example.demo_clean_code.data.model.UserEntity

sealed class UserIntent {
    data object LoadUsers : UserIntent()
    data class AddUser(val name: String, val email: String, val imagePath: String?) : UserIntent()
    data class DeleteUser(val user: UserEntity) : UserIntent()
    data object ClearUsers : UserIntent()
    data class SearchUser(val query: String) : UserIntent()
    data class UpdateName(val name: String) : UserIntent()
    data class UpdateEmail(val email: String) : UserIntent()
    data object UndoDelete : UserIntent()
}