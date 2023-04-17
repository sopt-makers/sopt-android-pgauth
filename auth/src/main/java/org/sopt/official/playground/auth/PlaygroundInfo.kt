package org.sopt.official.playground.auth

internal object PlaygroundInfo {
    val DEBUG_HOST: String = "sopt-internal-dev.pages.dev"
    val HOST: String = "playground.sopt.org"
    val AUTH_PATH: String = "auth/oauth"
    val REDIRECT_URI = "app://org.sopt.makers.android/oauth2redirect"
    val DEBUG_AUTH_HOST: String = "ec2-43-200-170-159.ap-northeast-2.compute.amazonaws.com:8080/api/v2/"
    val RELEASE_AUTH_HOST: String = "app.sopt.org/api/v2/"
}