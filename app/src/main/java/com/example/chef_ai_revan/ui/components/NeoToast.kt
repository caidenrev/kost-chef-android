package com.example.chef_ai_revan.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chef_ai_revan.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ─── Toast type ───────────────────────────────────────────────────────────────

enum class NeoToastType { SUCCESS, ERROR, INFO, WARNING, DELETE }

data class NeoToastData(
    val message: String,
    val type: NeoToastType = NeoToastType.SUCCESS,
    val id: Long = System.currentTimeMillis()
)

// ─── Global toast state ───────────────────────────────────────────────────────

object NeoToastState {
    private val _current = MutableStateFlow<NeoToastData?>(null)
    val current: StateFlow<NeoToastData?> = _current

    fun show(message: String, type: NeoToastType = NeoToastType.SUCCESS) {
        _current.value = NeoToastData(message = message, type = type)
    }

    fun dismiss() {
        _current.value = null
    }
}

// ─── Host overlay (pasang di root layout) ────────────────────────────────────

@Composable
fun NeoToastHost(modifier: Modifier = Modifier) {
    val toastData by NeoToastState.current.collectAsState()
    toastData?.let { data ->
        Box(
            modifier = modifier
                .fillMaxWidth()
                .zIndex(10f),
            contentAlignment = Alignment.TopEnd
        ) {
            NeoToastItem(data = data, onDismiss = { NeoToastState.dismiss() })
        }
    }
}

@Composable
private fun NeoToastItem(
    data: NeoToastData,
    onDismiss: () -> Unit
) {
    LaunchedEffect(data.id) {
        delay(2500)
        onDismiss()
    }

    val offsetY = remember { Animatable(-140f) }
    val scale  = remember { Animatable(0.75f) }

    LaunchedEffect(data.id) {
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    val (bgColor, icon) = when (data.type) {
        NeoToastType.SUCCESS -> NeoGreen  to Icons.Default.CheckCircle
        NeoToastType.ERROR   -> NeoPink   to Icons.Default.Cancel
        NeoToastType.WARNING -> NeoYellow to Icons.Default.Warning
        NeoToastType.DELETE  -> NeoPink   to Icons.Default.Delete
        NeoToastType.INFO    -> NeoCyan   to Icons.Default.Info
    }

    Box {
        // Shadow layer (offset ke kanan-bawah)
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationY = offsetY.value
                    scaleX = scale.value
                    scaleY = scale.value
                }
        ) {
            // Shadow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 4.dp, y = 4.dp)
                    .background(NeoBlack, shape = RoundedCornerShape(6.dp))
            )
            // Card utama
            Row(
                modifier = Modifier
                    .background(bgColor, shape = RoundedCornerShape(6.dp))
                    .border(2.dp, NeoBlack, shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeoBlack,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = data.message,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = NeoBlack
                )
            }
        }
    }
}

// ─── Neo-brutalist delete button ─────────────────────────────────────────────

@Composable
fun NeoDeleteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 32
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shadowOffset = if (isPressed) 1.dp else 3.dp
    val translation  = if (isPressed) 2.dp else 0.dp

    Box(
        modifier = modifier
            .offset(x = translation, y = translation)
            .neoShadow(
                color = NeoBlack,
                offsetX = shadowOffset,
                offsetY = shadowOffset,
                borderRadius = 6.dp
            )
            .background(NeoPink, shape = RoundedCornerShape(6.dp))
            .border(2.dp, NeoBlack, shape = RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Hapus",
            tint = NeoBlack,
            modifier = Modifier.size((size * 0.55f).dp)
        )
    }
}
