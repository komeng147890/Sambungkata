package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A beautiful, highly-interactive, and animated mascot character component to guide the child.
 * Features:
 * - Idle breathing & floating animations
 * - Interactive tap-to-bounce response with realistic squash & stretch physics
 * - Integrated SoundManager audio feedback
 * - Dynamic customizable speech bubble card with an arrow pointing to the mascot
 */
@Composable
fun AnimalMascot(
    emoji: String,
    speechText: String,
    modifier: Modifier = Modifier,
    subtitleText: String? = null,
    themeColor: Color = MaterialTheme.colorScheme.primary,
    isThinking: Boolean = false,
    showCorrect: Boolean = false,
    showIncorrect: Boolean = false
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Interactive physical animation states for tapping
    val bounceOffset = remember { Animatable(0f) }
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }

    // Continuous idle breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "MascotIdle")
    
    val idleScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleScale"
    )

    val idleRotation by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleRotation"
    )

    val idleOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleOffset"
    )

    // Function to run the bouncy jump animation
    val triggerJump: () -> Unit = {
        coroutineScope.launch {
            // Play cute sound effect
            SoundManager.playMascotTap()
            
            // 1. Squash downwards before jumping
            launch { scaleY.animateTo(0.75f, spring(stiffness = Spring.StiffnessHigh)) }
            launch { scaleX.animateTo(1.25f, spring(stiffness = Spring.StiffnessHigh)) }
            launch { rotation.animateTo(-5f, spring(stiffness = Spring.StiffnessHigh)) }
            delay(100)

            // 2. Launch upwards & stretch
            launch { bounceOffset.animateTo(-32f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)) }
            launch { scaleY.animateTo(1.3f, spring(stiffness = Spring.StiffnessHigh)) }
            launch { scaleX.animateTo(0.75f, spring(stiffness = Spring.StiffnessHigh)) }
            launch { rotation.animateTo(12f, spring(stiffness = Spring.StiffnessMediumLow)) }
            delay(220)

            // 3. Fall back down & brief land squash
            launch { bounceOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)) }
            launch { scaleY.animateTo(0.85f, spring(stiffness = Spring.StiffnessHigh)) }
            launch { scaleX.animateTo(1.15f, spring(stiffness = Spring.StiffnessHigh)) }
            launch { rotation.animateTo(-3f, spring(stiffness = Spring.StiffnessHigh)) }
            delay(120)

            // 4. Return to rest
            launch { scaleY.animateTo(1f, spring(stiffness = Spring.StiffnessMedium)) }
            launch { scaleX.animateTo(1f, spring(stiffness = Spring.StiffnessMedium)) }
            launch { rotation.animateTo(0f, spring(stiffness = Spring.StiffnessMedium)) }
        }
    }

    // Function to run the rapid wobble/shake animation for wrong answer
    val triggerShake: () -> Unit = {
        coroutineScope.launch {
            repeat(4) {
                launch { rotation.animateTo(-15f, tween(80, easing = LinearEasing)) }
                delay(80)
                launch { rotation.animateTo(15f, tween(80, easing = LinearEasing)) }
                delay(80)
            }
            launch { rotation.animateTo(0f, spring(stiffness = Spring.StiffnessMedium)) }
        }
    }

    // Auto-trigger animations based on ViewModel states
    LaunchedEffect(showCorrect) {
        if (showCorrect) {
            triggerJump()
        }
    }

    LaunchedEffect(showIncorrect) {
        if (showIncorrect) {
            triggerShake()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- MASCOT AVATAR ZONE ---
        Box(
            modifier = Modifier
                .size(76.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, // Custom physical bounce feels better than ripple
                    onClick = triggerJump
                )
                .testTag("animal_mascot_avatar"),
            contentAlignment = Alignment.Center
        ) {
            // Interactive soft glowing back aura
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .scale(idleScale)
                    .background(themeColor.copy(alpha = 0.12f), CircleShape)
                    .border(2.dp, themeColor.copy(alpha = 0.3f), CircleShape)
            )

            // Mascot Character Emoji with dynamic scaling, bouncing & rotation
            val currentEmoji = when {
                showCorrect -> "🥳"
                showIncorrect -> "🤔"
                isThinking -> "🧐"
                else -> emoji
            }

            Text(
                text = currentEmoji,
                fontSize = 42.sp,
                modifier = Modifier
                    .offset(y = (idleOffset + bounceOffset.value).dp)
                    .graphicsLayer {
                        // Blend continuous idle breathing with click-to-bounce physical animations
                        this.scaleX = scaleX.value * idleScale
                        this.scaleY = scaleY.value * idleScale
                        this.rotationZ = rotation.value + idleRotation
                    }
            )
        }

        // --- SPEECH BUBBLE ZONE ---
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Speech pointer/tail shape (A small triangle on the left side)
            Box(
                modifier = Modifier
                    .offset(x = (-8).dp)
                    .size(16.dp)
                    .clip(speechBubbleTailShape)
                    .background(Color.White)
                    .border(
                        BorderStroke(1.dp, themeColor.copy(alpha = 0.25f)),
                        speechBubbleTailShape
                    )
            )

            // Main dialogue container card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = themeColor.copy(alpha = 0.3f),
                        spotColor = themeColor.copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, themeColor.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = speechText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20),
                        lineHeight = 18.sp
                    )

                    if (subtitleText != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitleText,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF625B71)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom triangle shape representing the speech bubble arrow tail pointing left.
 */
private val speechBubbleTailShape = GenericShape { size, _ ->
    moveTo(size.width, 0f)
    lineTo(0f, size.height / 2f)
    lineTo(size.width, size.height)
    close()
}
