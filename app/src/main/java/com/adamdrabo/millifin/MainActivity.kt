package com.adamdrabo.millifin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamdrabo.millifin.content.ui.ContentSyncViewModel
import com.adamdrabo.millifin.core.navigation.MillifinApp
import com.adamdrabo.millifin.core.theme.MillifinTheme
import com.adamdrabo.millifin.settings.ui.AppStartState
import com.adamdrabo.millifin.settings.ui.AppStartViewModel
import com.adamdrabo.millifin.settings.ui.OnboardingScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            hiltViewModel<ContentSyncViewModel>()
            val appStartViewModel = hiltViewModel<AppStartViewModel>()
            val state by appStartViewModel.state.collectAsStateWithLifecycle()
            MillifinTheme {
                when (state) {
                    AppStartState.Loading -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    )
                    AppStartState.Onboarding -> OnboardingScreen(onStart = appStartViewModel::chooseCurrency)
                    is AppStartState.Ready -> MillifinApp()
                }
            }
        }
    }
}
