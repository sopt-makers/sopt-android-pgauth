package org.sopt.official.playground.auth.data

import org.sopt.official.playground.auth.data.remote.model.response.OAuthToken

interface PlaygroundAuthDatasource {
    suspend fun oauth(code: String): Result<OAuthToken>
}