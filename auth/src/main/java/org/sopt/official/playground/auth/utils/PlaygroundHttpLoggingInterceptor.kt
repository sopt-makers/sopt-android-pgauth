package org.sopt.official.playground.auth.utils

import okhttp3.logging.HttpLoggingInterceptor

internal class PlaygroundHttpLoggingInterceptor : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        PlaygroundLog.i(message)
    }
}