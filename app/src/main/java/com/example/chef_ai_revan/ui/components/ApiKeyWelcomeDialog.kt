package com.example.chef_ai_revan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chef_ai_revan.ui.theme.*

@Composable
fun ApiKeyWelcomeDialog(
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        NeoCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            backgroundColor = NeoWhite
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SELAMAT DATANG DI MASAKIN!",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Untuk pakai fitur AI Generator, masukkan Google Gemini API Key kamu lewat tombol pengaturan di header (ikon gerigi kuning, kanan atas).",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .neoShadow(offsetX = 3.dp, offsetY = 3.dp, borderRadius = 8.dp)
                        .background(NeoYellow.copy(alpha = 0.25f))
                        .border(2.dp, NeoBlack)
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .neoShadow(offsetX = 2.dp, offsetY = 2.dp, borderRadius = 20.dp)
                            .background(NeoYellow, CircleShape)
                            .border(2.dp, NeoBlack, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = NeoBlack,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Tombol Settings di pojok kanan atas",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = NeoBlack
                    )
                }

                Text(
                    text = "Dapatkan API Key gratis di aistudio.google.com",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeoPrimary,
                    textAlign = TextAlign.Center
                )

                NeoButton(
                    text = "ATUR API KEY SEKARANG",
                    onClick = onOpenSettings,
                    backgroundColor = NeoGreen,
                    modifier = Modifier.fillMaxWidth(),
                    borderRadius = 8.dp
                )
                NeoButton(
                    text = "NANTI",
                    onClick = onDismiss,
                    backgroundColor = Color.LightGray,
                    modifier = Modifier.fillMaxWidth(),
                    borderRadius = 8.dp
                )
            }
        }
    }
}
