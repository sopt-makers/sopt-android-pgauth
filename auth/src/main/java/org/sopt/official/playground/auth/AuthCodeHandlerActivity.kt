package org.sopt.official.playground.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import org.sopt.official.playground.auth.utils.PlaygroundLog
import org.sopt.official.playground.auth.utils.getParcelableAs

class AuthCodeHandlerActivity : AppCompatActivity() {
    private lateinit var uri: Uri
    private lateinit var state: String
    private lateinit var resultReceiver: ResultReceiver
    private var isAuthTabOpened: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlaygroundLog.i("onCreate")
        loadData()
        PlaygroundLog.i("uri: $uri")
        PlaygroundLog.i("state: $state")
    }

    private fun loadData() {
        intent.extras?.getBundle(Constants.AUTH_INTENT_BUNDLE_KEY)?.let {
            loadCustomTabData(it)
        } ?: sendError(PlaygroundError.IllegalConnect)
    }

    private fun loadCustomTabData(bundle: Bundle) = with(bundle) {
        getAuthUri()
        getAuthState()
        getResultReceiver()
    }

    private fun Bundle.getAuthUri() {
        uri = getParcelableAs<Uri>(Constants.AUTH_URI_KEY)
            ?: return sendError(PlaygroundError.IllegalConnect)
    }

    private fun Bundle.getAuthState() {
        state = getString(Constants.STATE)
            ?: return sendError(PlaygroundError.IllegalConnect)
    }

    private fun Bundle.getResultReceiver() {
        resultReceiver = getParcelableAs<ResultReceiver>(Constants.RESULT_RECEIVER)
            ?: return sendError(PlaygroundError.IllegalConnect)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        PlaygroundLog.i("onNewIntent")
        setIntent(intent)
        loadAppLinkData()
    }

    private fun loadAppLinkData() {
        val appLinkData: Uri? = intent.data
        val appLinkCode: String? = appLinkData?.getQueryParameter(Constants.CODE)
        val appLinkState: String? = appLinkData?.getQueryParameter(Constants.STATE)
        if (appLinkCode == null) return sendError(PlaygroundError.IllegalConnect)
        if (appLinkState == null) return sendError(PlaygroundError.IllegalConnect)
        PlaygroundLog.i("code: $appLinkCode")
        PlaygroundLog.i("state :$appLinkState")
        PlaygroundLog.i("redirect_uri: $uri")
        PlaygroundLog.i("redirect_state: $state")
        if (appLinkState != state) return sendError(PlaygroundError.InconsistentStateCode)
        sendOK(appLinkCode)
    }

    override fun onResume() {
        super.onResume()
        if (!isAuthTabOpened) {
            isAuthTabOpened = true
            launchPlaygroundAuth()
        }
    }

    private fun launchPlaygroundAuth() {
        CustomTabsIntent.Builder()
            .setUrlBarHidingEnabled(true)
            .setShowTitle(false)
            .build()
            .launchUrl(this, uri)
    }

    private fun sendOK(code: String) {
        resultReceiver.send(
            Activity.RESULT_OK,
            Bundle().apply { putString(Constants.STATE, code) }
        )
        finish()
    }

    private fun sendError(exception: PlaygroundError) {
        if (!this::resultReceiver.isInitialized && exception is PlaygroundError.IllegalConnect) {
            throw exception
        }
        resultReceiver.send(
            Activity.RESULT_CANCELED,
            Bundle().apply { putParcelable(Constants.EXCEPTION, exception) }
        )
        finish()
    }
}