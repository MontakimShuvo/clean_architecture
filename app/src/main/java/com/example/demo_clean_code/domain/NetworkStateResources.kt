package com.example.demo_clean_code.domain

sealed class NetworkStateResources<out t> {
    data class Success<out T>(val data: T) : NetworkStateResources<T>()
    data class Error(val message: String) : NetworkStateResources<Nothing>()
    object Loading : NetworkStateResources<Nothing>()
}
