package com.example.qafilah.features.assistant.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.qafilah.R
import com.example.ui_kit.components.assistant.AssistantTopAppBar
import com.example.ui_kit.components.assistant.QafilahChatInputBar
import com.example.ui_kit.components.assistant.QafilahTextMessage
import com.example.ui_kit.components.home.ProductCard
import com.example.ui_kit.components.home.ProductUiModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: AssistantViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Auto-scroll
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    // Show error Toast and clear it
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
            MaterialTheme.colorScheme.background
        )
    )

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                inputText = matches[0]
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }
            try {
                speechRecognizerLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Speech-to-text not supported on this device", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Microphone permission is required to use voice input.", Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundBrush)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                AssistantTopAppBar(
                    title = stringResource(id = R.string.ai_name),
                    subtitle = stringResource(id = R.string.ai_subtitle),
                    onBackClick = onNavigateBack,
                    onMoreClick = { viewModel.clearChat() },
                    painterResource(R.drawable.ic_arrow_back)
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    QafilahChatInputBar(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholderText = stringResource(id = R.string.ask_ai_placeholder),
                        onSendClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        onMicClick = {
                            val permissionCheck = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            )

                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
                                }
                                try {
                                    speechRecognizerLauncher.launch(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Speech-to-text not supported on this device", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(state.messages) { message ->
                    val isUser = message.role == "user"

                    if (isUser) {
                        QafilahTextMessage(
                            text = message.content,
                            timestamp = formatTimestamp(message.timestamp),
                            senderName = stringResource(id = R.string.you),
                            isUser = true
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (message.products.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(message.products) { product ->
                                        ProductCard(
                                            product = product,
                                            onClick = { onProductClick(product.id) },
                                            onFavoriteClick = { },
                                            toggleWishlistContentDescription = "Toggle wishlist",
                                            modifier = Modifier.width(140.dp)
                                        )
                                    }
                                }
                            }

                            QafilahTextMessage(
                                text = message.content,
                                timestamp = formatTimestamp(message.timestamp),
                                senderName = stringResource(id = R.string.ai_name),
                                isUser = false
                            )
                        }
                    }
                }

                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}