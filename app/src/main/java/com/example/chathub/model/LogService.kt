package com.example.chathub.model

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}