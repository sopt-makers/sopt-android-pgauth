package org.sopt.official.playground.auth.data

import org.sopt.official.playground.auth.PlaygroundError
import org.sopt.official.playground.auth.data.remote.AuthService
import org.sopt.official.playground.auth.data.remote.model.request.RequestToken
import org.sopt.official.playground.auth.data.remote.model.response.OauthToken
import java.net.UnknownHostException

internal class RemotePlaygroundAuthDatasource(
    private val authService: AuthService
) : PlaygroundAuthDatasource {

    override suspend fun oauth(code: String): Result<OauthToken> {
        val result = kotlin.runCatching { authService.oauth(RequestToken(code)) }
        return when (val exception = result.exceptionOrNull()) {
            null -> result
            is UnknownHostException -> Result.failure(PlaygroundError.NetworkUnavailable)
            else -> Result.failure(exception)
        }
    }
}