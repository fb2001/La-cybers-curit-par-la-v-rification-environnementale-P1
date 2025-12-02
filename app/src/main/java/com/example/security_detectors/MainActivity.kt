package com.example.security_detectors

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.security_detectors.detectors.SecurityDetectors
import com.example.security_detectors.ui.theme.Security_detectorsTheme
import kotlinx.coroutines.delay

// Palette de couleurs personnalisée
object AppColors {
    val Bordeaux = Color(0xFF8B0000)        // Bordeaux foncé
    val BordeauxLight = Color(0xFFB22222)   // Bordeaux clair
    val BordeauxVeryLight = Color(0xFFFFE5E5) // Bordeaux très clair
    val Black = Color(0xFF1A1A1A)           // Noir profond
    val DarkGray = Color(0xFF2D2D2D)        // Gris foncé
    val MediumGray = Color(0xFF404040)      // Gris moyen
    val LightGray = Color(0xFFE0E0E0)       // Gris clair
    val White = Color(0xFFFFFFFF)           // Blanc pur
    val OffWhite = Color(0xFFF5F5F5)        // Blanc cassé
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Security_detectorsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppColors.OffWhite
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = { SecurityTopBar() },
                        containerColor = AppColors.OffWhite
                    ) { innerPadding ->
                        SecurityStatusView(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityTopBar() {
    TopAppBar(
        title = {
            Text(
                "Détection de Sécurité",
                fontWeight = FontWeight.Bold,
                color = AppColors.White
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppColors.Bordeaux,
            titleContentColor = AppColors.White
        )
    )
}

@Composable
fun SecurityStatusView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var debugger by remember { mutableStateOf(false) }
    var devMode by remember { mutableStateOf(false) }
    var rooted by remember { mutableStateOf(false) }
    var emulator by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        debugger = SecurityDetectors.isDebuggerConnected()
        devMode = SecurityDetectors.isDeveloperModeEnabled(context)
        rooted = SecurityDetectors.isDeviceRooted()
        emulator = SecurityDetectors.isEmulator()
        delay(300)
        isLoading = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // En-tête avec statut global
        SecurityOverviewCard(
            debugger = debugger,
            devMode = devMode,
            rooted = rooted,
            emulator = emulator,
            isLoading = isLoading
        )

        // Cartes de détection individuelles
        AnimatedVisibility(
            visible = !isLoading,
            enter = fadeIn(tween(300)) + slideInVertically(tween(300))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SecurityDetectionCard(
                    title = "Débogueur",
                    isDetected = debugger,
                    icon = Icons.Default.Build,
                    severity = SecuritySeverity.HIGH
                )

                SecurityDetectionCard(
                    title = "Mode Développeur",
                    isDetected = devMode,
                    icon = Icons.Default.Settings,
                    severity = SecuritySeverity.MEDIUM
                )

                SecurityDetectionCard(
                    title = "Appareil Rooté",
                    isDetected = rooted,
                    icon = Icons.Default.Lock,
                    severity = SecuritySeverity.CRITICAL
                )

                SecurityDetectionCard(
                    title = "Émulateur",
                    isDetected = emulator,
                    icon = Icons.Default.Phone,
                    severity = SecuritySeverity.LOW
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppColors.Bordeaux)
            }
        }
    }
}

@Composable
fun SecurityOverviewCard(
    debugger: Boolean,
    devMode: Boolean,
    rooted: Boolean,
    emulator: Boolean,
    isLoading: Boolean
) {
    val threatsDetected = listOf(debugger, devMode, rooted, emulator).count { it }
    val isSecure = threatsDetected == 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isLoading -> AppColors.DarkGray
                isSecure -> AppColors.Black
                else -> AppColors.Bordeaux
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isLoading) "Analyse en cours..."
                    else if (isSecure) "Appareil Sécurisé"
                    else "Menaces Détectées",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isLoading) "Vérification de la sécurité..."
                    else "$threatsDetected menace(s) identifiée(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.White.copy(alpha = 0.8f)
                )
            }

            Icon(
                imageVector = if (isLoading) Icons.Default.Search
                else if (isSecure) Icons.Default.CheckCircle
                else Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = AppColors.White
            )
        }
    }
}

enum class SecuritySeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

@Composable
fun SecurityDetectionCard(
    title: String,
    isDetected: Boolean,
    icon: ImageVector,
    severity: SecuritySeverity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDetected) {
                when (severity) {
                    SecuritySeverity.CRITICAL -> AppColors.Bordeaux
                    SecuritySeverity.HIGH -> AppColors.BordeauxLight
                    SecuritySeverity.MEDIUM -> AppColors.MediumGray
                    SecuritySeverity.LOW -> AppColors.DarkGray
                }
            } else {
                AppColors.White
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isDetected)
                                AppColors.White.copy(alpha = 0.2f)
                            else
                                AppColors.Black.copy(alpha = 0.08f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isDetected) AppColors.White else AppColors.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDetected) AppColors.White else AppColors.Black
                    )
                }
            }

            // Badge de statut
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDetected) AppColors.White else AppColors.Bordeaux
            ) {
                Text(
                    text = if (isDetected) "Détecté" else "OK",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDetected) AppColors.Bordeaux else AppColors.White
                )
            }
        }
    }
}