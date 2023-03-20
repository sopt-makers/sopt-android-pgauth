package org.sopt.official.playground.auth.data.remote

import org.sopt.official.playground.auth.data.remote.model.request.RequestToken
import org.sopt.official.playground.auth.data.remote.model.response.OauthToken
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AuthService {
    @POST("oauth")
    suspend fun oauth(
        @Body request: RequestToken
    ): OauthToken
}