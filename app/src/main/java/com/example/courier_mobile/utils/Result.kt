package com.example.courier_mobile.utils

sealed class Result<out T> {
    data class Success<out T>(val data: T?) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()

    companion object {
        fun <T> success(data: T?) = Success(data)
        fun error(message: String) = Error(message)
        fun loading() = Loading
    }
}