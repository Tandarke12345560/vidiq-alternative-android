package com.vidiqalternative.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.YouTube
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    viewModel: SetupViewModel = hiltViewModel(),
    onSetupComplete: () -> Unit = {}
) {
    val youtubeKey by viewModel.youtubeKey.collectAsState()
    val openrouterKey by viewModel.openrouterKey.collectAsState()
    val isSetupComplete by viewModel.isSetupComplete.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showYoutubeKey by remember { mutableStateOf(false) }
    var showOpenrouterKey by remember { mutableStateOf(false) }

    LaunchedEffect(isSetupComplete) {
        if (isSetupComplete) {
            onSetupComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kurulum") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                imageVector = Icons.Default.Key,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "VidIQ Alternatif'e Hoş Geldiniz",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Devam etmek için API anahtarlarınızı girin",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // YouTube API Key
            OutlinedTextField(
                value = youtubeKey,
                onValueChange = { viewModel.updateYoutubeKey(it) },
                label = { Text("YouTube Data API Key") },
                leadingIcon = {
                    Icon(Icons.Default.YouTube, contentDescription = null)
                },
                trailingIcon = {
                    TextButton(onClick = { showYoutubeKey = !showYoutubeKey }) {
                        Text(if (showYoutubeKey) "Gizle" else "Göster")
                    }
                },
                visualTransformation = if (showYoutubeKey) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Google Console'dan alın: console.cloud.google.com",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // OpenRouter API Key
            OutlinedTextField(
                value = openrouterKey,
                onValueChange = { viewModel.updateOpenrouterKey(it) },
                label = { Text("OpenRouter API Key") },
                leadingIcon = {
                    Icon(Icons.Default.SmartToy, contentDescription = null)
                },
                trailingIcon = {
                    TextButton(onClick = { showOpenrouterKey = !showOpenrouterKey }) {
                        Text(if (showOpenrouterKey) "Gizle" else "Göster")
                    }
                },
                visualTransformation = if (showOpenrouterKey) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "openrouter.ai/keys adresinden alın",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Save button
            Button(
                onClick = { viewModel.saveAndContinue() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Kaydet ve Devam Et",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "API anahtarlarınız yalnızca cihazınızda saklanır\nHiçbir yere gönderilmez",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
