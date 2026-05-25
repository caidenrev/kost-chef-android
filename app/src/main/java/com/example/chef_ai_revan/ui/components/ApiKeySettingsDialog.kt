package com.example.chef_ai_revan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chef_ai_revan.ui.theme.*
import com.example.chef_ai_revan.viewmodel.BudgetViewModel

@Composable
fun ApiKeySettingsDialog(
    viewModel: BudgetViewModel,
    onDismiss: () -> Unit
) {
    val userApiKey by viewModel.userApiKey.collectAsStateWithLifecycle()
    val isDetectingModels by viewModel.isDetectingModels.collectAsStateWithLifecycle()
    val detectedModelInfo by viewModel.detectedModelInfo.collectAsStateWithLifecycle()
    val availableModels by viewModel.availableModels.collectAsStateWithLifecycle()

    var apiKeyInput by remember { mutableStateOf("") }

    LaunchedEffect(userApiKey) {
        if (apiKeyInput.isEmpty() && userApiKey != null) {
            apiKeyInput = userApiKey ?: ""
        }
        if (!userApiKey.isNullOrBlank() && viewModel.availableModels.value.isEmpty()) {
            viewModel.detectAvailableModels(userApiKey!!)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        NeoCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            backgroundColor = NeoWhite
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "PENGATURAN API KEY",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Text(
                    text = "Masukkan Google Gemini API Key dari Google AI Studio (aistudio.google.com).",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                NeoTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    placeholder = "Masukkan API Key (AIza...)",
                    modifier = Modifier.fillMaxWidth(),
                    borderRadius = 8.dp
                )

                if (isDetectingModels) {
                    NeoModelBadge(
                        backgroundColor = NeoCyan,
                        content = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = NeoBlack
                                )
                                Text(
                                    text = "Mendeteksi model tersedia...",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeoBlack
                                )
                            }
                        }
                    )
                } else if (detectedModelInfo != null) {
                    NeoModelBadge(
                        backgroundColor = if (availableModels.isNotEmpty()) NeoGreen else NeoPink,
                        content = {
                            Text(
                                text = detectedModelInfo ?: "",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = NeoBlack,
                                lineHeight = 16.sp
                            )
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeoButton(
                        text = "BATAL",
                        onClick = onDismiss,
                        backgroundColor = Color.LightGray,
                        modifier = Modifier.weight(1f),
                        borderRadius = 8.dp
                    )
                    NeoButton(
                        text = "SIMPAN",
                        onClick = {
                            viewModel.saveApiKey(apiKeyInput)
                            NeoToastState.show("API Key disimpan, mendeteksi model...", NeoToastType.INFO)
                        },
                        backgroundColor = NeoGreen,
                        modifier = Modifier.weight(1f),
                        borderRadius = 8.dp
                    )
                }

                if (!isDetectingModels && detectedModelInfo != null) {
                    NeoButton(
                        text = "TUTUP",
                        onClick = onDismiss,
                        backgroundColor = NeoBlack,
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        borderRadius = 8.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun NeoModelBadge(
    backgroundColor: Color,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .neoShadow(borderRadius = 8.dp)
            .background(backgroundColor, shape)
            .border(3.dp, NeoBlack, shape)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        content()
    }
}
