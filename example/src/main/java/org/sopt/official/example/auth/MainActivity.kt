package org.sopt.official.example.auth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.sopt.official.example.databinding.ActivityMainBinding
import org.sopt.official.playground.auth.PlaygroundAuth

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
        collectLoginDebugEvent()
    }

    private fun collectLoginEvent() = lifecycleScope.launch {
        authViewModel.loginEventStream.collect {
            PlaygroundAuth.authorizeWithWebTab(
                this@MainActivity,
                isDebug = false
            ) { result ->
                result.onSuccess {
                    authViewModel.completeLogin(it.accessToken)
                }.onFailure { exception ->
                }
            }
        }
    }

    private fun collectLoginDebugEvent() = lifecycleScope.launch {
        authViewModel.loginDebugEventStream.collect {
            PlaygroundAuth.authorizeWithWebTab(
                this@MainActivity,
                isDebug = true
            ) { result ->
                result.onSuccess {
                    authViewModel.completeLogin(it.accessToken)
                }.onFailure { exception ->
                }
            }
        }
    }
}