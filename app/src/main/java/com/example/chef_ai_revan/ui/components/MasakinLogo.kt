package com.example.chef_ai_revan.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chef_ai_revan.R

@Composable
fun MasakinLogo(
    modifier: Modifier = Modifier,
    height: Dp = 32.dp
) {
    Image(
        painter = painterResource(R.drawable.masakin_logo),
        contentDescription = "MASAKIN",
        modifier = modifier.height(height),
        contentScale = ContentScale.Fit
    )
}
