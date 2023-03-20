package org.sopt.official.playground.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class PlaygroundError(
    private val exceptionMessage: String
) : Exception(exceptionMessage), Parcelable {
    @Parcelize
    object IllegalConnect : PlaygroundError("비정상적인 접근 방법입니다.")

    @Parcelize
    object NotFoundStateCode : PlaygroundError("state 인증 코드를 찾을 수 없습니다")

    @Parcelize
    object IllegalStateCode : PlaygroundError("state 인증 코드를 처리할 수 없습니다.")

    @Parcelize
    object NetworkUnavailable : PlaygroundError("Network 에 연결할 수 없습니다.")

    @Parcelize
    object InconsistentStateCode : PlaygroundError("state 인증 코드가 일치하지 않습니다.")
}