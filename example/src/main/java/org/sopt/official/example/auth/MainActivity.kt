package org.sopt.official.example.auth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.sopt.official.example.databinding.ActivityMainBinding
import org.sopt.official.playground.auth.PlaygroundAuth
import org.sopt.official.playground.auth.utils.PlaygroundLog

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@MainActivity
            viewModel = authViewModel
        }.also {
            setContentView(it.root)
        }
        collectLoginEvent()
    }

    private fun collectLoginEvent() = lifecycleScope.launch {
        authViewModel.loginEventStream.collect() {
            kotlin.runCatching {
                PlaygroundAuth.authorizeWithWebTab(this@MainActivity) { result ->
                    result.onSuccess {
                        PlaygroundLog.i(it)
                        authViewModel.completeLogin(it.accessToken)
                    }.onFailure { throw it }
                }
            }.onFailure { authViewModel.completeLogin(it.stackTrace.joinToString { "\n" }) }
        }
    }
}