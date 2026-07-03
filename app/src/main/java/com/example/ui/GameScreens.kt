package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameApp(viewModel: GameViewModel) {
    val progressList by viewModel.allProgress.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(targetState = viewModel.currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
                GameScreen.Login -> LoginScreen(viewModel)
                GameScreen.Register -> RegisterScreen(viewModel)
                GameScreen.MainMenu -> MainMenuScreen(viewModel)
                GameScreen.LevelSelect -> LevelSelectScreen(viewModel, progressList)
                GameScreen.LevelInstructions -> LevelInstructionsScreen(viewModel)
                GameScreen.PlayLevel -> PlayLevelScreen(viewModel)
                GameScreen.VictoryScreen -> VictoryScreen(viewModel)
                GameScreen.GameOverScreen -> GameOverScreen(viewModel)
                GameScreen.Leaderboard -> LeaderboardScreen(viewModel)
                GameScreen.EditProfile -> EditProfileScreen(viewModel)
                GameScreen.OwnerDashboard -> OwnerDashboardScreen(viewModel)
            }
        }
    }
}

// 1. MAIN MENU SCREEN
@Composable
fun MainMenuScreen(viewModel: GameViewModel) {
    val infiniteTransition = rememberInfiniteTransition(label = "BouncingLogo")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoBounce"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFEF7FF), Color(0xFFF3EDF7))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // App Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sambung Kata",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .offset(y = bounceOffset.dp)
                            .testTag("app_title")
                    )
                    Text(
                        text = "Halo, ${viewModel.loggedInUser?.displayName ?: "Pemain"}! 👋",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Edit Profil ✏️",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable {
                                viewModel.clearProfileStatus()
                                viewModel.navigateTo(GameScreen.EditProfile)
                            }
                            .testTag("edit_profile_link")
                    )
                    if (viewModel.loggedInUser?.role == "owner") {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "🔑 Panel Owner / Guru ⭐",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100),
                            modifier = Modifier
                                .clickable {
                                    viewModel.loadAllUsers()
                                    viewModel.navigateTo(GameScreen.OwnerDashboard)
                                }
                                .testTag("owner_panel_link")
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var soundEnabled by remember { mutableStateOf(SoundManager.isSoundEnabled) }
                    IconButton(
                        onClick = {
                            SoundManager.isSoundEnabled = !SoundManager.isSoundEnabled
                            soundEnabled = SoundManager.isSoundEnabled
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                            .testTag("sound_toggle")
                    ) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Suara",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.error, CircleShape)
                            .testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Keluar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Text(
                text = "Petualangan Belajar Membaca & Mengeja!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF49454F),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Hero Graphic Banner (saved in drawable/img_game_banner)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 10.dp)
                .shadow(6.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_game_banner),
                    contentDescription = "Sambung Kata Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0x66000000))
                            )
                        )
                )
                Text(
                    text = "Ayo Main Bersama Teman Hewan! 🐾",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Action Buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { viewModel.navigateTo(GameScreen.LevelSelect) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(28.dp))
                    .testTag("mulai_button"),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MULAI BERMAIN",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Button(
                onClick = { viewModel.navigateTo(GameScreen.Leaderboard) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(28.dp))
                    .testTag("leaderboard_button"),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Leaderboard,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PAPAN SKOR",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.clearProfileStatus()
                    viewModel.navigateTo(GameScreen.EditProfile)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(28.dp))
                    .testTag("edit_profile_button"),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EDIT PROFIL SISWA",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            if (viewModel.loggedInUser?.role == "owner") {
                Button(
                    onClick = {
                        viewModel.loadAllUsers()
                        viewModel.navigateTo(GameScreen.OwnerDashboard)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFE0B2),
                        contentColor = Color(0xFFE65100)
                    ),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp)
                        .shadow(4.dp, RoundedCornerShape(28.dp))
                        .testTag("owner_dashboard_button"),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFFFB74D))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PANEL KONTROL OWNER",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            Button(
                onClick = { viewModel.resetAllProgress() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF757575)
                ),
                modifier = Modifier.testTag("reset_progress_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ulangi Semua Progress",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// 2. LEVEL SELECT SCREEN
@Composable
fun LevelSelectScreen(viewModel: GameViewModel, progressList: List<GameProgress>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Custom Back Header to match the "Sleek Interface" Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(GameScreen.MainMenu) },
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .testTag("back_to_menu_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali ke Menu Utama",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Pilih Tingkat",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20)
                )
                Text(
                    text = "Pilih petualangan hewanmu!",
                    fontSize = 12.sp,
                    color = Color(0xFF49454F)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Level Cards List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GameQuestions.levels.forEach { level ->
                val progress = progressList.find { it.levelId == level.id }
                val isUnlocked = progress?.isUnlocked ?: (level.id == 1)
                val stars = progress?.stars ?: 0
                val highScore = progress?.highScore ?: 0

                val bgGradient = if (isUnlocked) {
                    Brush.horizontalGradient(
                        colors = listOf(Color.White, Color(android.graphics.Color.parseColor(level.animalColor)).copy(alpha = 0.08f))
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFF3EDF7), Color(0xFFE6E0E9))
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (isUnlocked) 2.dp else 0.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(enabled = isUnlocked) { viewModel.selectLevel(level) }
                        .testTag("level_card_${level.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isUnlocked) MaterialTheme.colorScheme.outline.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .background(bgGradient)
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Animal Avatar
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(
                                        if (isUnlocked) Color(android.graphics.Color.parseColor(level.animalColor)).copy(alpha = 0.2f)
                                        else Color(0xFFCAC4D0),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = level.animalEmoji,
                                    fontSize = 28.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Level Info
                            Column {
                                Text(
                                    text = "Level ${level.id}: ${level.subtitle}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) MaterialTheme.colorScheme.primary else Color(0xFF49454F)
                                )
                                Text(
                                    text = level.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) Color(0xFF1D1B20) else Color(0xFF49454F).copy(alpha = 0.6f)
                                )
                                if (isUnlocked && highScore > 0) {
                                    Text(
                                        text = "Skor Tertinggi: $highScore",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF49454F)
                                    )
                                }
                            }
                        }

                        // Rightside lock/stars
                        if (!isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Terkunci",
                                tint = Color(0xFF49454F),
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            // Star rating
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(3) { index ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Bintang",
                                        tint = if (index < stars) Color(0xFFFBC02D) else Color(0xFFE6E0E9),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 3. LEVEL INSTRUCTIONS SCREEN
@Composable
fun LevelInstructionsScreen(viewModel: GameViewModel) {
    val level = viewModel.selectedLevel ?: return
    val animalColor = Color(android.graphics.Color.parseColor(level.animalColor))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Back Header to match the "Sleek Interface" top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(GameScreen.LevelSelect) },
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .testTag("back_to_levels_button_from_instructions")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali ke Pilih Level",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Beautiful animal instruction illustration
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Animated animal avatar bubble
            val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
            val scaleAnim by infiniteTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "AvatarPulse"
            )

            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(scaleAnim)
                    .background(animalColor.copy(alpha = 0.15f), CircleShape)
                    .border(4.dp, animalColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level.animalEmoji,
                    fontSize = 68.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Text Board in Sleek container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hai! Aku " + level.animalName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = level.description,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Custom non-deprecated divider matching sleek outline
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cara Bermain:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = level.instructions,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF49454F),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Start Button
        Button(
            onClick = { viewModel.startLevel(level.id) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(4.dp, RoundedCornerShape(28.dp))
                .testTag("start_level_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MAINKAN SEKARANG! 🎮",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// 4. MAIN GAMEPLAY SCREEN (COMPREHENSIVE FOR ALL LEVELS)
@Composable
fun PlayLevelScreen(viewModel: GameViewModel) {
    val level = viewModel.selectedLevel ?: return
    val animalColor = Color(android.graphics.Color.parseColor(level.animalColor))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFEF7FF), Color(0xFFF3EDF7))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Gameplay Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                IconButton(
                    onClick = { viewModel.navigateTo(GameScreen.LevelInstructions) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .testTag("back_to_instructions_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali ke Instruksi",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Hearts/Lives (not applicable to endless Level 4)
                if (level.id != 4) {
                    Row(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { index ->
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Nyawa",
                                tint = if (index < viewModel.currentLives) Color(0xFF6750A4) else Color(0xFFE6E0E9),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                } else {
                    // Level 4 Timer Bar
                    Row(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Sisa Waktu",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${viewModel.lightningTimerSeconds}s",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Score Display
                Row(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Skor",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${viewModel.currentScore}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                }
            }

            // LEVEL PUZZLE DISPATCHER
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (level.id) {
                    1 -> Level1And2PlayZone(viewModel, animalColor)
                    2 -> Level1And2PlayZone(viewModel, animalColor)
                    3 -> Level3BubblePlayZone(viewModel, animalColor)
                    4 -> Level4LightningPlayZone(viewModel, animalColor)
                    5 -> Level1And2PlayZone(viewModel, animalColor)
                    6 -> Level1And2PlayZone(viewModel, animalColor)
                    7 -> Level7SentencePlayZone(viewModel, animalColor)
                    8 -> Level8StoryPlayZone(viewModel, animalColor)
                }
            }

            // Bottom Alert Message / Hint
            AnimatedVisibility(
                visible = viewModel.hintText.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Petunjuk",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = viewModel.hintText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1D1B20)
                        )
                    }
                }
            }
        }

        // Correct / Incorrect Feedback Overlay
        if (viewModel.showCorrectAnimation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x226750A4)),
                contentAlignment = Alignment.Center
            ) {
                ConfettiCelebration()

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(24.dp))
                        .scale(1.05f),
                    border = BorderStroke(2.dp, Color(0xFF4CAF50))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎉 BENAR! 🎉",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        // Active animal character starts jumping and celebrating!
                        CharacterCorrectAnimation(emoji = level.animalEmoji)
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "+${viewModel.lastScoreAdded} SKOR",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFB300) // Beautiful Gold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Hebat sekali! ⭐",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20)
                        )
                    }
                }
            }
        }

        if (viewModel.showIncorrectAnimation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x22EF5350)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(24.dp))
                        .scale(1.05f),
                    border = BorderStroke(2.dp, Color(0xFFEF5350))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💪 COBA LAGI! 💪",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF5350)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Jangan menyerah, kamu pasti bisa! ❤️",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20)
                        )
                    }
                }
            }
        }
    }
}

// LEVEL 1 & 2 DESIGN
@Composable
fun Level1And2PlayZone(viewModel: GameViewModel, themeColor: Color) {
    val quiz = viewModel.currentQuiz ?: return
    val level = viewModel.selectedLevel ?: return
    var essayAnswer by remember(quiz.id) { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Animated animal guide mascot
        AnimalMascot(
            emoji = level.animalEmoji,
            speechText = if (level.id == 1 || level.id == 5) "Sambungkan kata dari suku kata terakhir!" else "Lengkapi kata kosong agar tersambung!",
            subtitleText = "Pertanyaan ${viewModel.activeQuestionIndex + 1} dari 5",
            themeColor = themeColor,
            showCorrect = viewModel.showCorrectAnimation,
            showIncorrect = viewModel.showIncorrectAnimation
        )

        // Active Word Board
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "KATA AWAL:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Emphasize the target linkage
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (level.id == 1 || level.id == 5) {
                        // Dynamic rendering for any number of syllables (Level 1 has 2, Level 5 has 3)
                        val parts = quiz.startSyllable.split(" - ")
                        parts.forEachIndexed { idx, part ->
                            Text(
                                text = part,
                                fontSize = if (idx == parts.lastIndex) 42.sp else 38.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (idx == parts.lastIndex) MaterialTheme.colorScheme.primary else Color(0xFF49454F)
                            )
                            if (idx < parts.lastIndex) {
                                Text(
                                    text = " - ",
                                    fontSize = 32.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    } else {
                        // LEVEL 2: e.g. BOLA -> LA-___
                        Text(
                            text = quiz.startWord,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "ke",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = quiz.targetSyllable + " - ",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF49454F)
                        )
                        Box(
                            modifier = Modifier
                                .width(54.dp)
                                .height(42.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "?",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Essay Input Panel
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TULIS JAWABANMU ✏️",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = themeColor
            )

            OutlinedTextField(
                value = essayAnswer,
                onValueChange = { newValue ->
                    if (newValue.length > essayAnswer.length) {
                        SoundManager.playSelectLetter()
                    } else if (newValue.length < essayAnswer.length) {
                        SoundManager.playDeselectLetter()
                    }
                    essayAnswer = newValue
                },
                placeholder = { Text("Ketik jawaban di sini...", color = Color.Gray) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723),
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (essayAnswer.isNotBlank()) {
                            viewModel.submitEssayAnswer(essayAnswer)
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .testTag("essay_input_level_${level.id}"),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF3E2723),
                    unfocusedTextColor = Color(0xFF3E2723),
                    focusedBorderColor = themeColor,
                    unfocusedBorderColor = Color(0xFFC4A484),
                    focusedContainerColor = Color(0xFFFAFAFA),
                    unfocusedContainerColor = Color(0xFFFDFDFD)
                )
            )

            Button(
                onClick = {
                    if (essayAnswer.isNotBlank()) {
                        viewModel.submitEssayAnswer(essayAnswer)
                    }
                },
                enabled = essayAnswer.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(52.dp)
                    .testTag("submit_essay_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColor,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "KIRIM JAWABAN 🚀",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

// LEVEL 3 DESIGN: FLOATING BUBBLES
@Composable
fun Level3BubblePlayZone(viewModel: GameViewModel, themeColor: Color) {
    val quiz = viewModel.level3Quiz ?: return
    val level = viewModel.selectedLevel ?: return

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Animated animal guide mascot
        AnimalMascot(
            emoji = level.animalEmoji,
            speechText = "Pecahkan gelembung sabun kata berurutan!",
            subtitleText = "Teka-teki ${viewModel.activeQuestionIndex + 1} dari 5",
            themeColor = themeColor,
            showCorrect = viewModel.showCorrectAnimation,
            showIncorrect = viewModel.showIncorrectAnimation
        )

        // Current Connected Chain Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "RANTAI KATA SAAT INI:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Initial
                    Text(
                        text = quiz.startWord,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF49454F)
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "sambung",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(16.dp)
                    )

                    // First popped
                    if (viewModel.level3PoppedBubbles.isNotEmpty()) {
                        Text(
                            text = viewModel.level3PoppedBubbles[0],
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "[ ??? ]",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "sambung",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(16.dp)
                    )

                    // Second popped
                    if (viewModel.level3PoppedBubbles.size >= 2) {
                        Text(
                            text = viewModel.level3PoppedBubbles[1],
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "[ ??? ]",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        // Bubbles Area (Glossy Lavender Spheres Playground)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(12.dp)
        ) {
            // Render 6 bubbles with distinct floating paths
            quiz.allBubbles.forEachIndexed { idx, bubbleWord ->
                val alreadyPopped = viewModel.level3PoppedBubbles.contains(bubbleWord)

                if (!alreadyPopped) {
                    val infiniteTransition = rememberInfiniteTransition(label = "FloatBubble")
                    // Distinct durations and offsets to make bubbles look random
                    val duration = 1800 + (idx * 240)
                    val offsetVal by infiniteTransition.animateFloat(
                        initialValue = -12f,
                        targetValue = 12f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(duration, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "BubbleOffset"
                    )

                    // Layout positioning based on index
                    val alignModifier = when (idx) {
                        0 -> Modifier.align(Alignment.TopStart)
                        1 -> Modifier.align(Alignment.TopEnd)
                        2 -> Modifier.align(Alignment.CenterStart)
                        3 -> Modifier.align(Alignment.CenterEnd)
                        4 -> Modifier.align(Alignment.BottomStart)
                        else -> Modifier.align(Alignment.BottomEnd)
                    }

                    Box(
                        modifier = alignModifier
                            .offset(y = offsetVal.dp, x = (offsetVal / 1.5f).dp)
                            .padding(6.dp)
                            .size(90.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFEADDFF), MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
                                )
                            )
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { viewModel.popBubble(bubbleWord) }
                            .testTag("bubble_$idx"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = bubbleWord,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// LEVEL 4 DESIGN: LIGHTNING ROUND
@Composable
fun Level4LightningPlayZone(viewModel: GameViewModel, themeColor: Color) {
    val level = viewModel.selectedLevel

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Active challenge info with AnimalMascot
        if (level != null) {
            AnimalMascot(
                emoji = level.animalEmoji,
                speechText = "Ayo sambung terus kartumu secepat kilat!",
                subtitleText = "Dapatkan skor sebanyak mungkin!",
                themeColor = themeColor,
                showCorrect = viewModel.showCorrectAnimation,
                showIncorrect = viewModel.showIncorrectAnimation
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Ayo sambung terus kartumu secepat kilat!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                    Text(
                        text = "Dapatkan skor sebanyak mungkin!",
                        fontSize = 11.sp,
                        color = Color(0xFF49454F)
                    )
                }
            }
        }

        // Active Word Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "KATA SEKARANG:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val word = viewModel.currentLightningWord
                    if (word.length >= 4) {
                        val base = word.substring(0, word.length - 2)
                        val end = word.substring(word.length - 2)
                        Text(
                            text = base,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF49454F)
                        )
                        Text(
                            text = end,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = word,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Mulai kata selanjutnya dengan: " + viewModel.currentLightningWord.takeLast(2).uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF49454F)
                )
            }
        }

        // Choice Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(viewModel.lightningOptions) { index, word ->
                Card(
                    modifier = Modifier
                        .height(56.dp)
                        .shadow(1.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectLightningWord(word) }
                        .testTag("lightning_option_$index"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = word,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20)
                        )
                    }
                }
            }
        }
    }
}

// 5. VICTORY SCREEN
@Composable
fun VictoryScreen(viewModel: GameViewModel) {
    val level = viewModel.selectedLevel ?: return
    var showLevelUpModal by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        SoundManager.playCheer()
    }

    val nextLevel = remember(level) {
        val nextId = level.id + 1
        GameQuestions.levels.find { it.id == nextId }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFEF7FF), Color(0xFFF3EDF7))
                    )
                )
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Animal cheering
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "HOREEE! 🎉",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = level.animalEmoji,
                        fontSize = 58.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Kamu Berhasil Menyelamatkan Level Ini!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20),
                    textAlign = TextAlign.Center
                )
            }

            // Stars & Score
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Large stars animation
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(3) { idx ->
                            val infiniteTransition = rememberInfiniteTransition(label = "StarPulse")
                            val scale by infiniteTransition.animateFloat(
                                initialValue = 0.9f,
                                targetValue = 1.1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(600 + (idx * 150), easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "StarScale"
                            )

                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Bintang",
                                tint = if (idx < viewModel.earnedStars) Color(0xFFFFB300) else Color(0xFFE6E0E9),
                                modifier = Modifier
                                    .size(48.dp)
                                    .scale(if (idx < viewModel.earnedStars) scale else 1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Bintang Diperoleh: " + viewModel.earnedStars + " / 3",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF49454F)
                    )
                    Text(
                        text = "Skor Kamu: " + viewModel.currentScore,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(GameScreen.LevelSelect) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("back_to_levels_button")
                    ) {
                        Text(
                            text = "PILIH LEVEL 🗺️",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            val nextId = level.id + 1
                            val nextLevelData = GameQuestions.levels.find { it.id == nextId }
                            if (nextLevelData != null) {
                                viewModel.selectLevel(nextLevelData)
                            } else {
                                viewModel.navigateTo(GameScreen.LevelSelect)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("next_level_button")
                    ) {
                        Text(
                            text = "LANJUT ➡️",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }

        // Beautiful Level Up celebration popup overlay!
        if (showLevelUpModal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable(enabled = false) {}
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(450)) + scaleIn(
                        initialScale = 0.82f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                    exit = fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.8f)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 4.dp)
                            .shadow(24.dp, RoundedCornerShape(32.dp)),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(6.dp, Color(0xFFFFC107))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(
                                    text = "👑",
                                    fontSize = 54.sp,
                                    modifier = Modifier.offset(y = (-18).dp)
                                )
                                Text(
                                    text = "✨",
                                    fontSize = 32.sp,
                                    modifier = Modifier.offset(x = (-40).dp, y = (-5).dp)
                                )
                                Text(
                                    text = "✨",
                                    fontSize = 32.sp,
                                    modifier = Modifier.offset(x = 40.dp, y = 5.dp)
                                )
                            }

                            Text(
                                text = if (nextLevel != null) "NAIK LEVEL! 🚀" else "JUARA DUNIA! 🏆",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFE65100),
                                textAlign = TextAlign.Center
                            )

                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFF8E1),
                                border = BorderStroke(2.dp, Color(0xFFFFD54F)),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "⭐", fontSize = 16.sp)
                                    Text(
                                        text = if (nextLevel != null) "LEVEL BARU TERBUKA" else "SEMUA TANTANGAN SELESAI",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF795548),
                                        letterSpacing = 1.sp
                                    )
                                    Text(text = "⭐", fontSize = 16.sp)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .background(Color(0xFFFFF3E0), CircleShape)
                                    .border(4.dp, Color(0xFFFFB74D), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = nextLevel?.animalEmoji ?: "👑",
                                    fontSize = 48.sp
                                )
                            }

                            Text(
                                text = nextLevel?.animalName ?: "Hebat Sekali!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = if (nextLevel != null) {
                                    "Hore! Kamu berhasil membuka tantangan baru bersama teman hewanmu yang menggemaskan! Mari pelajari: ${nextLevel.subtitle}!"
                                } else {
                                    "Luar biasa! Kamu telah melahap habis seluruh petualangan dan menyusun ribuan kata secara sempurna!"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF5D4037),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    showLevelUpModal = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = "LANJUTKAN 🎮",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 6. GAME OVER SCREEN
@Composable
fun GameOverScreen(viewModel: GameViewModel) {
    val level = viewModel.selectedLevel ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFEF7FF), Color(0xFFF3EDF7))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Animal comforting
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "YAH, NYAWA HABIS! 😢",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFBA1A1A),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(Color(0x18BA1A1A), CircleShape)
                    .border(3.dp, Color(0xFFBA1A1A), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level.animalEmoji,
                    fontSize = 58.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Jangan bersedih! " + level.animalName + " ingin kamu mencoba lagi!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                textAlign = TextAlign.Center
            )
        }

        // Bottom motivational prompt
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ayo asah terus kemampuan membacamu. Sedikit lagi kamu pasti bisa!",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF49454F),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { viewModel.navigateTo(GameScreen.LevelSelect) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("gameover_map_button")
            ) {
                Text(
                    text = "PILIH LEVEL 🗺️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Button(
                onClick = { viewModel.startLevel(level.id) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A)),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .weight(1.3f)
                    .height(52.dp)
                    .testTag("retry_level_button")
            ) {
                Text(
                    text = "COBA LAGI 🔄",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}

// --- LOGIN SCREEN ---
@Composable
fun LoginScreen(viewModel: GameViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoggingInAsGuest by remember { mutableStateOf(false) }

    // Clear errors when entering screen
    DisposableEffect(Unit) {
        viewModel.clearErrors()
        onDispose {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF8F0), Color(0xFFFFF2E6))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Mascot / Cute Icon
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(Color(0xFFFFEAD2), CircleShape)
                .border(3.dp, Color(0xFFFFA500), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🐱",
                fontSize = 54.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Sambung Kata",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFE65C00),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Bermain sambil belajar membaca bersama teman hewan!",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B5A2B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Card container for inputs
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(2.dp, Color(0xFFFFE2C4))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Play as Guest (Child Friendly Quick Start)
                Button(
                    onClick = {
                        if (!isLoggingInAsGuest) {
                            isLoggingInAsGuest = true
                            val uniqueGuestId = "tamu${(1000..9999).random()}"
                            viewModel.register(
                                username = uniqueGuestId,
                                displayName = "Pemain Cilik",
                                passwordHash = "tamu123"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("guest_login_button"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6), // Beautiful Purple
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isLoggingInAsGuest) "Menyiapkan Game... ⏳" else "MAIN SEBAGAI TAMU 🚀",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f).height(1.5.dp).background(Color(0xFFE5D5C5)))
                    Text(
                        text = "atau masuk dengan akun",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5A2B)
                    )
                    Box(modifier = Modifier.weight(1f).height(1.5.dp).background(Color(0xFFE5D5C5)))
                }

                // Error alert
                viewModel.loginError?.let { err ->
                    Text(
                        text = "⚠️ $err",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(12.dp))
                            .border(1.5.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    )
                }

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nama Pengguna (Username)", fontWeight = FontWeight.Bold) },
                    placeholder = { Text("Contoh: budi123", color = Color.Gray) },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF3E2723)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .testTag("login_username_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF3E2723),
                        unfocusedTextColor = Color(0xFF3E2723),
                        focusedBorderColor = Color(0xFFE65C00),
                        unfocusedBorderColor = Color(0xFFC4A484),
                        focusedLabelColor = Color(0xFFE65C00),
                        unfocusedLabelColor = Color(0xFF8B5A2B),
                        focusedContainerColor = Color(0xFFFAFAFA),
                        unfocusedContainerColor = Color(0xFFFDFDFD)
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color(0xFFE65C00))
                    }
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Kata Sandi (Password)", fontWeight = FontWeight.Bold) },
                    placeholder = { Text("Masukkan kata sandi Anda", color = Color.Gray) },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF3E2723)
                    ),
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .testTag("login_password_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF3E2723),
                        unfocusedTextColor = Color(0xFF3E2723),
                        focusedBorderColor = Color(0xFFE65C00),
                        unfocusedBorderColor = Color(0xFFC4A484),
                        focusedLabelColor = Color(0xFFE65C00),
                        unfocusedLabelColor = Color(0xFF8B5A2B),
                        focusedContainerColor = Color(0xFFFAFAFA),
                        unfocusedContainerColor = Color(0xFFFDFDFD)
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFFE65C00))
                    },
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = icon, contentDescription = "Tampilkan Password", tint = Color(0xFF8B5A2B))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { viewModel.login(username, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("login_button"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE65C00),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "MASUK KE AKUN 🔓",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Belum punya akun? ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8B5A2B)
            )
            Text(
                text = "Daftar Sekarang",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD84315),
                modifier = Modifier
                    .clickable { viewModel.navigateTo(GameScreen.Register) }
                    .testTag("go_to_register_button")
            )
        }
    }
}

// --- REGISTER SCREEN ---
@Composable
fun RegisterScreen(viewModel: GameViewModel) {
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Clear errors when entering screen
    DisposableEffect(Unit) {
        viewModel.clearErrors()
        onDispose {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFEF7FF), Color(0xFFF3EDF7))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✍️",
                fontSize = 40.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Daftar Akun Baru",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Buat akun untuk memulai petualangan bermain sambil belajar!",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF49454F),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "REGISTRASI",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                viewModel.registerError?.let { err ->
                    Text(
                        text = "⚠️ $err",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )
                }

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("register_username_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                )

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Nama Tampilan (Panggilan)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("register_display_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Face, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password (Min. 4 karakter)") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().testTag("register_password_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = icon, contentDescription = "Tampilkan Password")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.register(username, displayName, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("register_button"),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "DAFTAR SEKARANG",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Sudah punya akun? ",
                fontSize = 14.sp,
                color = Color(0xFF49454F)
            )
            Text(
                text = "Masuk Di Sini",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { viewModel.navigateTo(GameScreen.Login) }
                    .testTag("go_to_login_button")
            )
        }
    }
}

// --- 9. LEADERBOARD SCREEN ---
@Composable
fun LeaderboardScreen(viewModel: GameViewModel) {
    val leaderboardEntries by viewModel.leaderboard.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(GameScreen.MainMenu) },
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .testTag("back_from_leaderboard_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali ke Menu Utama",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Papan Skor Terbaik",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cute Trophy Icon Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🏆",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = "Lima Skor Tertinggi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Ayo ukir prestasimu di papan skor!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (leaderboardEntries.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "✨",
                        fontSize = 50.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "Belum Ada Skor Tercatat",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Selesaikan level game untuk mencatat skormu pertama kali!",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // List of top 5 scores
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                leaderboardEntries.take(5).forEachIndexed { index, entry ->
                    val rankEmoji = when (index) {
                        0 -> "🏆"
                        1 -> "🥈"
                        2 -> "🥉"
                        else -> "${index + 1}"
                    }

                    val rankColor = when (index) {
                        0 -> Color(0xFFFFF176) // Gold Light
                        1 -> Color(0xFFE0E0E0) // Silver Light
                        2 -> Color(0xFFFFB74D) // Bronze Light
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(12.dp))
                            .testTag("leaderboard_item_$index"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (index < 3) rankColor.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (index < 3) rankColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rank display
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(rankColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = rankEmoji,
                                    fontSize = if (index < 3) 18.sp else 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (index < 3) Color.Unspecified else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Name & Level info
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = entry.username,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = entry.levelName,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Score info
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${entry.score}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "⭐",
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- LEVEL 7 DESIGN: LENGKAPI KALIMAT ---
@Composable
fun Level7SentencePlayZone(viewModel: GameViewModel, themeColor: Color) {
    val quiz = viewModel.currentSentenceQuiz ?: return
    val level = viewModel.selectedLevel ?: return
    var essayAnswer by remember(quiz.id) { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Animated animal guide mascot
        AnimalMascot(
            emoji = level.animalEmoji,
            speechText = "Bantu Poli melengkapi kalimat!",
            subtitleText = "Pertanyaan ${viewModel.activeQuestionIndex + 1} dari 5",
            themeColor = themeColor,
            showCorrect = viewModel.showCorrectAnimation,
            showIncorrect = viewModel.showIncorrectAnimation
        )

        // Active Word/Sentence Board
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LENGKAPI KALIMAT:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = quiz.sentence,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D1B20),
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )
            }
        }

        // Essay Input Panel
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TULIS KATA YANG HILANG ✏️",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = themeColor
            )

            OutlinedTextField(
                value = essayAnswer,
                onValueChange = { newValue ->
                    if (newValue.length > essayAnswer.length) {
                        SoundManager.playSelectLetter()
                    } else if (newValue.length < essayAnswer.length) {
                        SoundManager.playDeselectLetter()
                    }
                    essayAnswer = newValue
                },
                placeholder = { Text("Ketik kata di sini...", color = Color.Gray) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723),
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (essayAnswer.isNotBlank()) {
                            viewModel.submitEssayAnswer(essayAnswer)
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .testTag("essay_input_level_${level.id}"),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF3E2723),
                    unfocusedTextColor = Color(0xFF3E2723),
                    focusedBorderColor = themeColor,
                    unfocusedBorderColor = Color(0xFFC4A484),
                    focusedContainerColor = Color(0xFFFAFAFA),
                    unfocusedContainerColor = Color(0xFFFDFDFD)
                )
            )

            Button(
                onClick = {
                    if (essayAnswer.isNotBlank()) {
                        viewModel.submitEssayAnswer(essayAnswer)
                    }
                },
                enabled = essayAnswer.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(52.dp)
                    .testTag("submit_essay_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColor,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "KIRIM JAWABAN 🚀",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

// --- LEVEL 8 DESIGN: SOAL CERITA PENDEK ---
@Composable
fun Level8StoryPlayZone(viewModel: GameViewModel, themeColor: Color) {
    val quiz = viewModel.currentStoryQuiz ?: return
    val level = viewModel.selectedLevel ?: return
    var essayAnswer by remember(quiz.id) { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Animated animal guide mascot
        AnimalMascot(
            emoji = level.animalEmoji,
            speechText = "Bantu Tito menjawab pertanyaan!",
            subtitleText = "Pertanyaan ${viewModel.activeQuestionIndex + 1} dari 5",
            themeColor = themeColor,
            showCorrect = viewModel.showCorrectAnimation,
            showIncorrect = viewModel.showIncorrectAnimation
        )

        // Story Board Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF6)), // Cozy off-white color for reading
            border = BorderStroke(1.dp, Color(0xFFE6D7B8)) // Soft sepia border
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "📖 BACALAH CERITA DI BAWAH INI:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8D6E63) // Cozy brown
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = quiz.story,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF3E2723), // Warm dark brown
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                // Beautiful spacer line representing Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE6D7B8))
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Pertanyaan:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = quiz.question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20),
                    lineHeight = 22.sp
                )
            }
        }

        // Essay Input Panel
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TULIS JAWABANMU ✏️",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = themeColor
            )

            OutlinedTextField(
                value = essayAnswer,
                onValueChange = { newValue ->
                    if (newValue.length > essayAnswer.length) {
                        SoundManager.playSelectLetter()
                    } else if (newValue.length < essayAnswer.length) {
                        SoundManager.playDeselectLetter()
                    }
                    essayAnswer = newValue
                },
                placeholder = { Text("Ketik jawaban di sini...", color = Color.Gray) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723),
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (essayAnswer.isNotBlank()) {
                            viewModel.submitEssayAnswer(essayAnswer)
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .testTag("essay_input_level_${level.id}"),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF3E2723),
                    unfocusedTextColor = Color(0xFF3E2723),
                    focusedBorderColor = themeColor,
                    unfocusedBorderColor = Color(0xFFC4A484),
                    focusedContainerColor = Color(0xFFFAFAFA),
                    unfocusedContainerColor = Color(0xFFFDFDFD)
                )
            )

            Button(
                onClick = {
                    if (essayAnswer.isNotBlank()) {
                        viewModel.submitEssayAnswer(essayAnswer)
                    }
                },
                enabled = essayAnswer.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(52.dp)
                    .testTag("submit_essay_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColor,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "KIRIM JAWABAN 🚀",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

// --- CHARACTER CORRECT ANIMATIONS ---
@Composable
fun CharacterCorrectAnimation(emoji: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(150.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating sparkles and star particles
        FloatingStarsEffect()

        // Jumping animal with squash-and-stretch
        JumpingAnimalAnimation(emoji = emoji)
    }
}

@Composable
fun JumpingAnimalAnimation(emoji: String, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "jumping_animal")

    // Vertical jump offset animation (0 to -45dp)
    val bounceOffset by infiniteTransition.animateValue(
        initialValue = 0.dp,
        targetValue = (-45).dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 600
                0.dp at 0 with FastOutSlowInEasing
                (-45).dp at 280 with LinearOutSlowInEasing
                0.dp at 560 with FastOutLinearInEasing
                0.dp at 600
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "bounce"
    )

    // Squash and stretch scale animation (height/width scaling to make it feel organic!)
    val scaleY by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 600
                1f at 0 with FastOutSlowInEasing
                0.85f at 60 with LinearEasing // Squish on start
                1.25f at 240 with FastOutSlowInEasing // Stretch on rise
                1.0f at 300 with LinearEasing
                1.0f at 500 with FastOutLinearInEasing
                0.8f at 560 with LinearEasing // Squish on land!
                1f at 600
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "scaleY"
    )

    val scaleX by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 600
                1f at 0 with FastOutSlowInEasing
                1.15f at 60 with LinearEasing // Bulge out on start squish
                0.8f at 240 with FastOutSlowInEasing // Slim down on stretch
                1.0f at 300 with LinearEasing
                1.0f at 500 with FastOutLinearInEasing
                1.2f at 560 with LinearEasing // Bulge out on land squish
                1f at 600
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "scaleX"
    )

    // Little rotation wobble to make it even more fun!
    val rotation by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 250, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    // Shadow scale animation underneath the jumping animal
    val shadowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 600
                1f at 0 with FastOutSlowInEasing
                0.4f at 280 with LinearOutSlowInEasing
                1f at 560 with FastOutLinearInEasing
                1f at 600
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "shadowScale"
    )

    val shadowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 600
                0.25f at 0 with FastOutSlowInEasing
                0.05f at 280 with LinearOutSlowInEasing
                0.25f at 560 with FastOutLinearInEasing
                0.25f at 600
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "shadowAlpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.height(130.dp)
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.weight(1f)
        ) {
            // Shadow that shrinks as the animal jumps higher
            Box(
                modifier = Modifier
                    .width(44.dp * shadowScale)
                    .height(8.dp * shadowScale)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = shadowAlpha))
            )

            // The actual animal emoji jumping and squishing
            Text(
                text = emoji,
                fontSize = 58.sp,
                modifier = Modifier
                    .offset(y = bounceOffset)
                    .graphicsLayer {
                        this.scaleY = scaleY
                        this.scaleX = scaleX
                        this.rotationZ = rotation
                    }
            )
        }
    }
}

@Composable
fun FloatingStarsEffect() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Box(modifier = Modifier.size(150.dp)) {
        val stars = listOf(
            Triple(-45f, -35f, "✨"),
            Triple(45f, -25f, "⭐"),
            Triple(-35f, 30f, "⭐"),
            Triple(45f, 35f, "✨"),
            Triple(0f, -65f, "🎉")
        )

        stars.forEachIndexed { i, star ->
            val delayShift = (i * 0.2f)
            val currentProgress = (animProgress + delayShift) % 1f

            val offsetX = star.first * currentProgress
            val offsetY = star.second * currentProgress - (45f * currentProgress) // floats upwards
            val alpha = 1f - currentProgress
            val scale = 0.4f + (currentProgress * 0.9f)

            Text(
                text = star.third,
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = offsetX.dp, y = offsetY.dp)
                    .graphicsLayer {
                        this.alpha = alpha
                        this.scaleX = scale
                        this.scaleY = scale
                    }
            )
        }
    }
}

// --- CONFETTI CELEBRATION COMPONENT ---
enum class ConfettiShape {
    Circle, Rectangle, Triangle
}

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    val size: Float,
    val color: Color,
    var speedX: Float,
    var speedY: Float,
    var rotation: Float,
    val rotationSpeed: Float,
    val shape: ConfettiShape
)

@Composable
fun ConfettiCelebration() {
    val colors = remember {
        listOf(
            Color(0xFFFF7A00), Color(0xFFFFC700), Color(0xFF8B5CF6),
            Color(0xFF14B8A6), Color(0xFFF43F5E), Color(0xFF4CAF50), Color(0xFF2196F3)
        )
    }

    val particles = remember {
        val list = mutableListOf<ConfettiParticle>()
        repeat(75) {
            val angle = Math.random() * 2 * Math.PI
            val speed = 5f + (Math.random() * 15f).toFloat()
            list.add(
                ConfettiParticle(
                    x = 0f,
                    y = 0f,
                    size = 12f + (Math.random() * 16f).toFloat(),
                    color = colors.random(),
                    speedX = (Math.cos(angle) * speed).toFloat(),
                    speedY = -(5f + (Math.random() * 20f).toFloat()),
                    rotation = (Math.random() * 360f).toFloat(),
                    rotationSpeed = (-10f + (Math.random() * 20f).toFloat()),
                    shape = ConfettiShape.values().random()
                )
            )
        }
        list
    }

    var frameTime by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { time ->
                frameTime = time
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        LaunchedEffect(width, height) {
            particles.forEach { p ->
                if (p.x == 0f && p.y == 0f) {
                    val side = Math.random()
                    if (side < 0.33) {
                        p.x = width * 0.15f
                        p.y = height * 0.9f
                        p.speedX = 6f + (Math.random() * 14f).toFloat()
                    } else if (side < 0.66) {
                        p.x = width * 0.85f
                        p.y = height * 0.9f
                        p.speedX = -(6f + (Math.random() * 14f).toFloat())
                    } else {
                        p.x = width * 0.5f
                        p.y = height * 0.7f
                        p.speedX = -8f + (Math.random() * 16f).toFloat()
                    }
                }
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = frameTime // Read value to trigger draw on each frame
            
            particles.forEach { p ->
                if (p.x != 0f || p.y != 0f) {
                    p.x += p.speedX
                    p.y += p.speedY
                    p.speedY += 0.4f // Gravity
                    p.speedX *= 0.98f // Air resistance
                    p.rotation += p.rotationSpeed

                    if (p.y < height + 50) {
                        rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                            when (p.shape) {
                                ConfettiShape.Circle -> {
                                    drawCircle(
                                        color = p.color,
                                        radius = p.size / 2,
                                        center = Offset(p.x, p.y)
                                    )
                                }
                                ConfettiShape.Rectangle -> {
                                    drawRect(
                                        color = p.color,
                                        topLeft = Offset(p.x - p.size / 2, p.y - p.size / 4),
                                        size = Size(p.size, p.size / 2)
                                    )
                                }
                                ConfettiShape.Triangle -> {
                                    val path = Path().apply {
                                        moveTo(p.x, p.y - p.size / 2)
                                        lineTo(p.x - p.size / 2, p.y + p.size / 2)
                                        lineTo(p.x + p.size / 2, p.y + p.size / 2)
                                        close()
                                    }
                                    drawPath(path = path, color = p.color)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- ANIMAL MASCOT COMPONENT ---
@Composable
fun AnimalMascot(
    emoji: String,
    animalName: String,
    guidanceText: String,
    subText: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
) {
    // Gentle floating/breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_breathing")
    val floatY by infiniteTransition.animateValue(
        initialValue = 0.dp,
        targetValue = (-6).dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    // Subtle rotation/sway wobble to feel organic
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotate"
    )

    // Interactive squish/scale feedback on click
    var isTapped by remember { mutableStateOf(false) }
    val tapScale by animateFloatAsState(
        targetValue = if (isTapped) 1.25f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tapScale"
    )

    LaunchedEffect(isTapped) {
        if (isTapped) {
            delay(300)
            isTapped = false
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(24.dp))
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable { isTapped = true }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("animal_mascot_${animalName.lowercase().replace(" ", "_")}")
    ) {
        // Rounded avatar container for animal emoji
        Box(
            modifier = Modifier
                .size(56.dp)
                .offset(y = floatY)
                .graphicsLayer {
                    this.scaleX = tapScale
                    this.scaleY = tapScale
                    this.rotationZ = rotationAngle
                }
                .background(Color.White, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), CircleShape)
                .shadow(1.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Bubble dialogue text with rich styling
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "💬 $animalName",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = guidanceText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                lineHeight = 18.sp
            )
            if (subText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subText,
                    fontSize = 11.sp,
                    color = Color(0xFF49454F)
                )
            }
        }
    }
}


// 10. EDIT PROFILE SCREEN
@Composable
fun EditProfileScreen(viewModel: GameViewModel) {
    val user = viewModel.loggedInUser ?: return

    val avatars = listOf("🧒", "👧", "🐱", "🐰", "🐒", "🦁", "🐼", "🦊", "🐨", "🦄", "🚀", "🎨")
    
    // Parse the current avatar & display name
    val initialDisplayName = user.displayName
    var selectedAvatar by remember {
        mutableStateOf(avatars.find { initialDisplayName.startsWith(it) } ?: "🧒")
    }
    var displayNameInput by remember {
        mutableStateOf(
            if (avatars.any { initialDisplayName.startsWith(it) }) {
                initialDisplayName.substring(2).trim()
            } else {
                initialDisplayName
            }
        )
    }
    var passwordInput by remember { mutableStateOf(user.passwordHash) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFEF7FF), Color(0xFFF3EDF7))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    SoundManager.playMascotTap()
                    viewModel.navigateTo(GameScreen.MainMenu)
                },
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White, CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                    .testTag("profile_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Edit Profil Siswa",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Current Avatar Preview
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFFFFF8E1), CircleShape)
                            .border(4.dp, Color(0xFFFFC107), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedAvatar,
                            fontSize = 54.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Pilih Karakter Kamu 🌟",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF5D4037)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Avatar Selector Grid (Simple Rows of 6 items each)
                    val avatarsRow1 = avatars.take(6)
                    val avatarsRow2 = avatars.drop(6)
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            avatarsRow1.forEach { avatar ->
                                AvatarChip(avatar = avatar, isSelected = selectedAvatar == avatar) {
                                    SoundManager.playMascotTap()
                                    selectedAvatar = avatar
                                }
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            avatarsRow2.forEach { avatar ->
                                AvatarChip(avatar = avatar, isSelected = selectedAvatar == avatar) {
                                    SoundManager.playMascotTap()
                                    selectedAvatar = avatar
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Nickname Input Field
                    OutlinedTextField(
                        value = displayNameInput,
                        onValueChange = { displayNameInput = it },
                        label = { Text("Nama Panggilan Siswa") },
                        placeholder = { Text("Masukkan nama panggilan...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = Color(0xFFF9F6FA),
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Input Field
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Kata Sandi") },
                        placeholder = { Text("Masukkan kata sandi baru...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text = if (passwordVisible) "🙈" else "👁️",
                                    fontSize = 20.sp
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = Color(0xFFF9F6FA),
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_password_input")
                    )

                    // Error Message
                    viewModel.profileError?.let { err ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Save Action Button
                Button(
                    onClick = {
                        val finalDisplayName = "$selectedAvatar $displayNameInput".trim()
                        viewModel.updateProfile(finalDisplayName, passwordInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("save_profile_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SIMPAN PERUBAHAN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Success dialog overlay
        if (viewModel.profileSuccess) {
            LaunchedEffect(Unit) {
                SoundManager.playCorrect()
            }
            AlertDialog(
                onDismissRequest = {
                    viewModel.clearProfileStatus()
                    viewModel.navigateTo(GameScreen.MainMenu)
                },
                title = {
                    Text(
                        text = "Berhasil! 🎉",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color(0xFF2E7D32)
                    )
                },
                text = {
                    Text(
                        text = "Profil siswa kamu telah diperbarui dengan sukses! Karakter pilihanmu sekarang siap menemanimu bertualang.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF37474F)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearProfileStatus()
                            viewModel.navigateTo(GameScreen.MainMenu)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text(
                            text = "OK, LANJUT 🎮",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun AvatarChip(avatar: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .background(
                if (isSelected) Color(0xFFFFE082) else Color(0xFFF5F5F5),
                CircleShape
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color(0xFFFFB300) else Color(0xFFE0E0E0),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .testTag("avatar_option_$avatar"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = avatar,
            fontSize = 24.sp
        )
    }
}

// --- 10. OWNER / TEACHER DASHBOARD SCREEN ---
@Composable
fun OwnerDashboardScreen(viewModel: GameViewModel) {
    val usersList = viewModel.allUsersList
    val loggedInUser = viewModel.loggedInUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFEF7FF), Color(0xFFF3EDF7))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(GameScreen.MainMenu) },
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .testTag("back_from_owner_dashboard_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali ke Menu Utama",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Panel Kontrol Owner ⭐",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Stats Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Statistik Pengguna",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total Pengguna",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${usersList.size}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column {
                        Text(
                            text = "Owner/Guru",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${usersList.count { it.role == "owner" }}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                    Column {
                        Text(
                            text = "Siswa",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${usersList.count { it.role != "owner" }}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1565C0)
                        )
                    }
                }
            }
        }

        // List Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Daftar Pengguna (${usersList.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Segarkan 🔄",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { viewModel.loadAllUsers() }
                    .padding(4.dp)
            )
        }

        // User list container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (usersList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Tidak ada pengguna terdaftar.", color = Color.Gray)
                }
            } else {
                usersList.forEach { user ->
                    val isCurrentUser = user.username == loggedInUser?.username

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrentUser) Color(0xFFE8F5E9) else Color.White
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isCurrentUser) Color(0xFF81C784) else Color(0xFFEEEEEE)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                if (user.role == "owner") Color(0xFFFFE0B2) else Color(0xFFE3F2FD),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (user.role == "owner") "👑" else "👶",
                                            fontSize = 20.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = user.displayName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Color(0xFF333333)
                                            )
                                            if (isCurrentUser) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "(Anda)",
                                                    fontSize = 11.sp,
                                                    color = Color.Gray,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                        Text(
                                            text = "ID: ${user.username}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                // Role Badge
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (user.role == "owner") Color(0xFFFF9800) else Color(0xFF9E9E9E),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = user.role.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFEEEEEE)))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Action buttons row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Toggle Role Button
                                Button(
                                    onClick = {
                                        val newRole = if (user.role == "owner") "siswa" else "owner"
                                        viewModel.updateUserRole(user, newRole)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (user.role == "owner") Color(0xFFECEFF1) else Color(0xFFE3F2FD),
                                        contentColor = if (user.role == "owner") Color(0xFF37474F) else Color(0xFF1565C0)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                ) {
                                    Text(
                                        text = if (user.role == "owner") "Ubah Jadi Siswa" else "Ubah Jadi Owner",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Unlock / Complete Game Button
                                Button(
                                    onClick = {
                                        viewModel.unlockAllLevelsForUser(user.username)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE8F5E9),
                                        contentColor = Color(0xFF2E7D32)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Buka Level",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Delete Button
                                IconButton(
                                    onClick = {
                                        viewModel.deleteUserByOwner(user.username)
                                    },
                                    enabled = !isCurrentUser,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (isCurrentUser) Color(0xFFF5F5F5) else Color(0xFFFFEBEE),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus Pengguna",
                                        tint = if (isCurrentUser) Color.LightGray else Color(0xFFD32F2F),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



