package com.ark.arkpad.ui.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun FadeIn(
    modifier: Modifier = Modifier,
    animationSpec: AnimationSpec<Float> = tween<Float>(),
    content: @Composable BoxScope.() -> Unit,
) {
    var isVisible by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = animationSpec,
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = modifier
            .alpha(contentAlpha)
            .wrapContentSize(),
        content = content,
    )
}