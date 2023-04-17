package org.sopt.official.playground.auth.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RequestToken(
    @SerialName("code")
    val code: String
)