package org.sopt.official.playground.auth.data

import okhttp3.OkHttpClient
import org.sopt.official.playground.auth.Constants
import org.sopt.official.playground.auth.PlaygroundInfo
import org.sopt.official.playground.auth.data.remote.AuthService
import org.sopt.official.playground.auth.data.remote.ServiceFactory
import retrofit2.Retrofit

internal class PlaygroundApiManager private constructor(
    private val retrofit: Retrofit
) {
    fun provideAuthService(): AuthService = retrofit.create(AuthService::class.java)

    companion object {
        @Volatile
        private var instance: PlaygroundApiManager? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: PlaygroundApiManager(createRetrofit()).also {
                instance = it
            }
        }

        private fun createRetrofit() = ServiceFactory.withClient(
            url = "${Constants.SCHEME}://${PlaygroundInfo.authHost}",
            client = OkHttpClient.Builder()
                .addInterceptor(ServiceFactory.loggingInterceptor)
                .build()
        )
    }
}