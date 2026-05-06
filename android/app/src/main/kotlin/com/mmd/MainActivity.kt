package com.mmd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mmd.core.common.MmdConstants
import com.mmd.core.design.component.MmdGreetingCard
import com.mmd.core.design.theme.MmdTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MmdTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeRoot()
                }
            }
        }
    }
}

@Composable
private fun HomeRoot() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MmdGreetingCard(message = MmdConstants.APP_NAME)
    }
}
