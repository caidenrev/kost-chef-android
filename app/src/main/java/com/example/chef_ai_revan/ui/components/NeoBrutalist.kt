package com.example.chef_ai_revan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chef_ai_revan.ui.theme.*

private val NeoBorderWidth = 3.dp
private val NeoShadowOffset = 8.dp

fun Modifier.neoShadow(
    color: Color = NeoBlack,
    offsetX: Dp = NeoShadowOffset,
    offsetY: Dp = NeoShadowOffset,
    borderRadius: Dp = 12.dp
) = this.drawBehind {
    drawRoundRect(
        color = color,
        topLeft = Offset(offsetX.toPx(), offsetY.toPx()),
        size = size,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(borderRadius.toPx())
    )
}

@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    borderRadius: Dp = 12.dp,
    borderWidth: Dp = NeoBorderWidth,
    borderColor: Color = NeoBlack,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(borderRadius)
    Box(
        modifier = modifier
            .neoShadow(borderRadius = borderRadius)
            .background(backgroundColor, shape)
            .border(borderWidth, borderColor, shape)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun NeoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    contentColor: Color = NeoBlack,
    borderRadius: Dp = 12.dp,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(borderRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Tactile physical animation: when pressed, translate down-right, and shrink shadow offset
    val shadowOffset = if (enabled && isPressed) 3.dp else 8.dp
    val translation = if (enabled && isPressed) 5.dp else 0.dp

    Box(
        modifier = modifier
            .offset(x = translation, y = translation)
            .neoShadow(
                color = if (enabled) NeoBlack else NeoBlack.copy(alpha = 0.3f),
                offsetX = shadowOffset,
                offsetY = shadowOffset,
                borderRadius = borderRadius
            )
            .background(if (enabled) backgroundColor else Color.LightGray, shape)
            .border(NeoBorderWidth, if (enabled) NeoBlack else NeoBlack.copy(alpha = 0.5f), shape)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Disable default material ripple to prioritize brutalist press effect
                enabled = enabled,
                onClick = onClick
            )
            .padding(vertical = 12.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = LocalTextStyle.current.copy(
                fontWeight = FontWeight.Black,
                color = if (enabled) contentColor else NeoBlack.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    backgroundColor: Color = Color.White,
    borderRadius: Dp = 12.dp
) {
    val shape = RoundedCornerShape(borderRadius)
    Box(
        modifier = modifier
            .neoShadow(borderRadius = borderRadius)
            .background(backgroundColor, shape)
            .border(NeoBorderWidth, NeoBlack, shape)
            .padding(16.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = NeoBlack.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = LocalTextStyle.current.copy(
                fontWeight = FontWeight.Bold,
                color = NeoBlack
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F2F8)
@Composable
fun NeoComponentsPreview() {
    ChefairevanTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            NeoButton(text = "Order Now", onClick = {}, backgroundColor = NeoPrimary, contentColor = Color.White)
            NeoCard(backgroundColor = NeoGreen) {
                Text("This is a Neo-Brutalist Card with rounded corners and rounded hard shadows.", fontWeight = FontWeight.Bold)
            }
            NeoTextField(value = "", onValueChange = {}, placeholder = "Enter budget...")
        }
    }
}
