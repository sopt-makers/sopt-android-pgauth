package org.sopt.official.playground.auth

import android.app.Activity
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import androidx.appcompat.app.AppCompatActivity
import org.sopt.official.playground.auth.utils.PlaygroundCustomTabManager
import org.sopt.official.playground.auth.utils.PlaygroundLog
import org.sopt.official.playground.auth.utils.getParcelableAs

class AuthCodeHandlerActivity : AppCompatActivity() {
    private lateinit var uri: Uri
    private lateinit var state: String
    private lateinit var resultReceiver: ResultReceiver

    private var isAuthTabOpened: Boolean = false
    private var authTabConnection: ServiceConnection? = null
    private var internalHandler: Handler? = null

    private var newIntentState = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlaygroundLog.i("onCreate")
        loadData()
    }

    private fun loadData() {
        PlaygroundLog.i("load data")
        intent.extras?.getBundle(Constants.AUTH_INTENT_BUNDLE_KEY)?.let {
            loadCustomTabData(it)
        } ?: sendError(PlaygroundError.IllegalConnect("bundle data was not provided"))
    }

    private fun loadCustomTabData(bundle: Bundle) = with(bundle) {
        PlaygroundLog.i("load custom tab data")
        getAuthUri()
        getAuthState()
        getResultReceiver()
        internalHandler = Handler(Looper.getMainLooper()) {
            sendError(PlaygroundError.Cancelled())
            true
        }
        PlaygroundLog.i("uri: $uri")
        PlaygroundLog.i("state: $state")
        PlaygroundLog.i("receiver: $resultReceiver")
    }

    private fun Bundle.getAuthUri() {
        uri = getParcelableAs<Uri>(Constants.AUTH_URI_KEY)
            ?: return sendError(PlaygroundError.IllegalConnect("uri was not provided"))
    }

    private fun Bundle.getAuthState() {
        state = getString(Constants.STATE)
            ?: return sendError(PlaygroundError.IllegalConnect("state was not provided"))
    }

    private fun Bundle.getResultReceiver() {
        resultReceiver = getParcelableAs<ResultReceiver>(Constants.RESULT_RECEIVER)
            ?: return sendError(PlaygroundError.IllegalConnect("resultReceiver was not provided"))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        newIntentState += 1
        PlaygroundLog.i("onNewIntent - state: $newIntentState")
        internalHandler?.hasMessages(0).let {
            if (it == true) {
                internalHandler?.removeMessages(0)
            }
        }
        internalHandler = null
        loadAppLinkData(intent ?: throw IllegalStateException("안휘 이게 웨 업워"))
    }

    private fun loadAppLinkData(intent: Intent) {
        val appLinkData: Uri = intent.data
            ?: return sendError(PlaygroundError.IllegalConnect("not found appLink data"))
        val appLinkCode: String = appLinkData.getQueryParameter(Constants.CODE)
            ?: return sendError(PlaygroundError.IllegalConnect("not found code"))
        val appLinkState: String = appLinkData.getQueryParameter(Constants.STATE)
            ?: return sendError(PlaygroundError.IllegalConnect("not found state"))
        PlaygroundLog.i("code: $appLinkCode")
        PlaygroundLog.i("state :$appLinkState")
        PlaygroundLog.i("redirect_uri: $uri")
        PlaygroundLog.i("redirect_state: $state")

        if (appLinkState != state) return sendError(PlaygroundError.InconsistentStateCode())
        sendOK(appLinkCode)
        finish()
    }

    override fun onResume() {
        super.onResume()
        PlaygroundLog.i("onResume")
        if (!isAuthTabOpened) {
            isAuthTabOpened = true
            launchPlaygroundAuth()
        } else {
            internalHandler?.hasMessages(0).let {
                if (it == false) {
                    internalHandler?.sendEmptyMessageDelayed(0, 100)
                }
            }
        }
    }

    override fun onDestroy() {
        authTabConnection?.let { unbindService(it) }
        super.onDestroy()
    }

    private fun launchPlaygroundAuth() {
        PlaygroundCustomTabManager.openWithDefaultBrowser(this, uri)
            .onSuccess { authTabConnection = it }
            .onFailure { openUnBindServiceBrowser() }
    }

    private fun openUnBindServiceBrowser() {
        PlaygroundCustomTabManager.open(this, uri).onFailure { exception ->
            PlaygroundLog.w(exception)
            if (exception is PlaygroundError) {
                sendError(exception)
            } else {
                throw exception
            }
        }
    }

    private fun sendOK(code: String) {
        resultReceiver.send(
            Activity.RESULT_OK,
            Bundle().apply { putString(Constants.STATE, code) }
        )
        finish()
    }

    private fun sendError(exception: PlaygroundError) {
        if (exception is PlaygroundError.IllegalConnect) {
            throw exception
        }
        resultReceiver.send(
            Activity.RESULT_CANCELED,
            Bundle().apply { putParcelable(Constants.EXCEPTION, exception) }
        )
        finish()
    }
}