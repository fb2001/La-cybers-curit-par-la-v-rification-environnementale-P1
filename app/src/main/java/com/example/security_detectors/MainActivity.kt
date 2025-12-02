package com.example.security_detectors

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.security_detectors.ui.theme.Security_detectorsTheme
import com.example.security_detectors.detectors.SecurityDetectors
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.LocalContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Security_detectorsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Passe "this" comme contexte pour les détecteurs qui en ont besoin
                    SecurityStatusView(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SecurityStatusView(modifier: Modifier = Modifier) {
    val debugger = SecurityDetectors.isDebuggerConnected()
    val devMode = SecurityDetectors.isDeveloperModeEnabled(LocalContext.current)
    val rooted = SecurityDetectors.isDeviceRooted()
    val emulator = SecurityDetectors.isEmulator()

    Column(modifier = modifier) {
        Text(text = "Debugger connected: $debugger")
        Text(text = "Developer mode: $devMode")
        Text(text = "Device rooted: $rooted")
        Text(text = "Running on emulator: $emulator")
    }
}

@Preview(showBackground = true)
@Composable
fun SecurityStatusPreview() {
    Security_detectorsTheme {
        SecurityStatusView()
    }
}