package org.sopt.official.playground.auth.data

import org.sopt.official.playground.auth.data.remote.model.response.OauthToken

interface PlaygroundAuthDatasource {
    suspend fun oauth(code: String): Result<OauthToken>
}