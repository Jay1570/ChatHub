package com.example.chathub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chathub.model.LogService
import com.example.chathub.snackbar.SnackbarManager
import com.example.chathub.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class ChatAppViewModel(private val logService: LogService) : ViewModel()  {
    fun launchCatching(snackbar: Boolean = true, block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch (
            CoroutineExceptionHandler { _, throwable ->
                if (snackbar) {
                    SnackbarManager.showMessage(throwable.toSnackbarMessage())
                }
                logService.logNonFatalCrash(throwable)
            },
            block = block
        )
}