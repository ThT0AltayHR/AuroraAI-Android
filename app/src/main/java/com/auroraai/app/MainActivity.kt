package com.auroraai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.auroraai.app.data.SettingsStore
import com.auroraai.app.ui.ChatScreen
import com.auroraai.app.ui.ImageScreen
import com.auroraai.app.ui.SettingsScreen
import com.auroraai.app.ui.market.AiMarketScreen
import com.auroraai.app.ui.onboarding.OnboardingScreen
import com.auroraai.app.ui.theme.AuroraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val settings = SettingsStore(applicationContext)

        setContent {
            AuroraTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNav(settings)
                }
            }
        }
    }
}

@Composable
fun AppNav(settings: SettingsStore) {
    val navController: NavHostController = rememberNavController()
    val startDestination = if (settings.isOnboardingComplete) "chat" else "onboarding"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") { OnboardingScreen(settings = settings, navController = navController) }
        composable("chat") { ChatScreen(settings = settings, navController = navController) }
        composable("image") { ImageScreen(settings = settings, navController = navController) }
        composable("settings") { SettingsScreen(settings = settings, navController = navController) }
        composable("market") { AiMarketScreen(settings = settings, navController = navController) }
    }
}
