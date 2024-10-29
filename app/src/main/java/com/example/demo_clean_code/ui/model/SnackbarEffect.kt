package com.example.demo_clean_code.ui.model

sealed class SnackbarEffect {
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : SnackbarEffect()
}