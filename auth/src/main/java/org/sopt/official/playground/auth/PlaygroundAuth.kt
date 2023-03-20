package org.sopt.official.playground.auth

import android.content.Context
import android.net.Uri
import android.os.ResultReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.sopt.official.playground.auth.data.PlaygroundApiManager
import org.sopt.official.playground.auth.data.PlaygroundAuthDatasource
import org.sopt.official.playground.auth.data.RemotePlaygroundAuthDatasource
import org.sopt.official.playground.auth.data.remote.model.response.OauthToken
import org.sopt.official.playground.auth.utils.PlaygroundUriProvider

object PlaygroundAuth {
    fun authorizeWithWebTab(
        context: Context,
        authStateGenerator: AuthStateGenerator = DefaultAuthStateGenerator(),
        uri: Uri? = null,
        authDataSource: PlaygroundAuthDatasource = RemotePlaygroundAuthDatasource(
            PlaygroundApiManager.getInstance().provideAuthService()
        ),
        callback: (Result<OauthToken>) -> Unit
    ) {
        val stateToken = authStateGenerator.generate()
        val resultReceiver = resultReceiver(authDataSource) {
            callback(it)
        }
        startAuthTab(
            context = context,
            uri = uri ?: PlaygroundUriProvider().authorize(state = stateToken),
            state = stateToken,
            resultReceiver = resultReceiver
        )
    }

    private fun startAuthTab(
        context: Context,
        uri: Uri,
        state: String,
        resultReceiver: ResultReceiver
    ) {
        AuthIntentFactory.authIntentWithAuthTab(
            context = context,
            uri = uri,
            state = state,
            resultReceiver = resultReceiver
        ).run { context.startActivity(this) }
    }

    private fun resultReceiver(
        authDataSource: PlaygroundAuthDatasource,
        callback: (Result<OauthToken>) -> Unit
    ) = PlaygroundAuthResultReceiver(
        callback = { code, error ->
            if (error != null) {
                callback(Result.failure(error as PlaygroundError))
            } else {
                code ?: throw IllegalStateException()
                requestOauthToken(authDataSource, code, callback)
            }
        }
    )

    private fun requestOauthToken(
        authDataSource: PlaygroundAuthDatasource,
        code: String,
        callback: (Result<OauthToken>) -> Unit
    ) = CoroutineScope(Dispatchers.Default).launch {
        callback(authDataSource.oauth(code))
    }
}