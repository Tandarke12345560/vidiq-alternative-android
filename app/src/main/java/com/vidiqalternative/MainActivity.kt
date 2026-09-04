package com.vidiqalternative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.vidiqalternative.data.repository.ApiKeyRepository
import com.vidiqalternative.ui.navigation.NavGraph
import com.vidiqalternative.ui.navigation.Screen
import com.vidiqalternative.ui.theme.VidIQTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var apiKeyRepository: ApiKeyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VidIQTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val isSetupComplete by apiKeyRepository.isSetupComplete.collectAsState(initial = null)

                    when (isSetupComplete) {
                        null -> {
                            // Loading
                        }
                        true -> {
                            NavGraph(
                                navController = navController,
                                startDestination = Screen.Home.route
                            )
                        }
                        false -> {
                            NavGraph(
                                navController = navController,
                                startDestination = Screen.Setup.route
                            )
                        }
                    }
                }
            }
        }
    }
}
