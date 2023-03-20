package org.sopt.official.playground.auth.data.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
internal data class RequestToken(
    val code: String
)