package com.example.captionstudio

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val msg: String = "", val error: Exception? = null) :
        Result<Nothing>()
}