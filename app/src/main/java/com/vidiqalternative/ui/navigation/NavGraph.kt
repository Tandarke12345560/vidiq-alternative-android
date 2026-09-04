package com.vidiqalternative.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vidiqalternative.ui.home.HomeScreen
import com.vidiqalternative.ui.search.SearchScreen
import com.vidiqalternative.ui.ai.AIChatScreen
import com.vidiqalternative.ui.settings.AISettingsScreen
import com.vidiqalternative.ui.analytics.AnalyticsScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object AIChat : Screen("ai_chat")
    data object AISettings : Screen("ai_settings")
    data object Analytics : Screen("analytics")
    data object VideoDetail : Screen("video/{videoId}") {
        fun createRoute(videoId: String) = "video/$videoId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onAICoachClick = { navController.navigate(Screen.AIChat.route) },
                onAnalyticsClick = { navController.navigate(Screen.Analytics.route) }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onBackClick = { navController.popBackStack() },
                onVideoClick = { videoId ->
                    navController.navigate(Screen.VideoDetail.createRoute(videoId))
                }
            )
        }

        composable(Screen.AIChat.route) {
            AIChatScreen(
                onSettingsClick = { navController.navigate(Screen.AISettings.route) }
            )
        }

        composable(Screen.AISettings.route) {
            AISettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.VideoDetail.route) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: return@composable
            VideoDetailScreen(
                videoId = videoId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    videoId: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Detay") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Video ID: $videoId")
        }
    }
}
