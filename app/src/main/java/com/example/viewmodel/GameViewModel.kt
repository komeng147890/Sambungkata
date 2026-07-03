package com.example.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.ui.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

enum class GameScreen {
    Login,
    Register,
    MainMenu,
    LevelSelect,
    LevelInstructions,
    PlayLevel,
    VictoryScreen,
    GameOverScreen,
    Leaderboard,
    EditProfile,
    OwnerDashboard
}

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModel(private val repository: GameRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            try {
                val nyiReni = User("nyi reni", "1234", "👧 NYI Reni", "owner")
                val registered = repository.registerUser(nyiReni)
                if (!registered) {
                    repository.updateUser(nyiReni)
                }
            } catch (e: Exception) {
                // Ignore pre-population errors
            }
        }
    }

    private val currentUsernameFlow = MutableStateFlow<String?>(null)

    // Expose Room database progress to UI dynamically per logged-in user
    val allProgress: StateFlow<List<GameProgress>> = currentUsernameFlow
        .flatMapLatest { username ->
            if (username != null) {
                repository.getAllProgress(username)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Expose Leaderboard Flow to UI
    val leaderboard: StateFlow<List<LeaderboardEntry>> = repository.top5Leaderboard
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Authentication States
    var loggedInUser: User? by mutableStateOf(null)
        private set

    var loginError by mutableStateOf<String?>(null)
        private set

    var registerError by mutableStateOf<String?>(null)
        private set

    var registerSuccess by mutableStateOf(false)
        private set

    var profileError by mutableStateOf<String?>(null)
        private set

    var profileSuccess by mutableStateOf(false)
        private set

    // Game state variables
    var currentScreen by mutableStateOf(GameScreen.Login)
        private set

    var selectedLevel: LevelData? by mutableStateOf(null)
        private set

    // Active gameplay variables
    var currentScore by mutableStateOf(0)
        private set

    var lastScoreAdded by mutableStateOf(0)
        private set

    var currentLives by mutableStateOf(3)
        private set

    var activeQuestionIndex by mutableStateOf(0)
        private set

    // Level 1 & 2 Specifics
    var currentQuiz: WordQuiz? by mutableStateOf(null)
        private set

    // Level 3 Specifics (Floating Bubble State)
    var level3Quiz: GameQuestions.BubbleQuiz? by mutableStateOf(null)
        private set
    var level3PoppedBubbles by mutableStateOf<List<String>>(emptyList())
        private set

    // Level 7 Specifics (Sentence Completion)
    var currentSentenceQuiz: SentenceQuiz? by mutableStateOf(null)
        private set

    // Level 8 Specifics (Story comprehension)
    var currentStoryQuiz: StoryQuiz? by mutableStateOf(null)
        private set

    // Level 4 Specifics (Lightning Cards)
    var currentLightningWord by mutableStateOf("")
        private set
    var lightningOptions by mutableStateOf<List<String>>(emptyList())
        private set
    var lightningTimerSeconds by mutableStateOf(30)
        private set
    private var timerJob: Job? = null

    // Feedback States
    var showCorrectAnimation by mutableStateOf(false)
        private set
    var showIncorrectAnimation by mutableStateOf(false)
        private set
    var lastSelectedOptionIndex by mutableStateOf(-1)
        private set
    var hintText by mutableStateOf("")
        private set

    // Score calculations on finish
    var earnedStars by mutableStateOf(0)
        private set

    // Authentication Methods
    fun login(username: String, passwordHash: String) {
        val uClean = username.trim()
        val pClean = passwordHash.trim()
        if (uClean.isBlank() || pClean.isBlank()) {
            loginError = "Username dan password tidak boleh kosong!"
            return
        }
        viewModelScope.launch {
            val user = repository.authenticateUser(uClean, pClean)
            if (user != null) {
                loggedInUser = user
                loginError = null
                repository.initializeProgressIfNeeded(user.username)
                currentUsernameFlow.value = user.username
                navigateTo(GameScreen.MainMenu)
            } else {
                loginError = "Username atau password salah!"
            }
        }
    }

    fun register(username: String, displayName: String, passwordHash: String) {
        val uClean = username.trim().lowercase()
        val dClean = displayName.trim()
        val pClean = passwordHash.trim()
        if (uClean.isBlank() || dClean.isBlank() || pClean.isBlank()) {
            registerError = "Semua kolom harus diisi!"
            return
        }
        if (uClean.length < 3) {
            registerError = "Username minimal 3 karakter!"
            return
        }
        if (pClean.length < 4) {
            registerError = "Password minimal 4 karakter!"
            return
        }
        viewModelScope.launch {
            val success = repository.registerUser(User(uClean, pClean, dClean))
            if (success) {
                registerSuccess = true
                registerError = null
                // Auto login immediately
                loggedInUser = User(uClean, pClean, dClean)
                currentUsernameFlow.value = uClean
                navigateTo(GameScreen.MainMenu)
            } else {
                registerError = "Username sudah terdaftar!"
            }
        }
    }

    fun logout() {
        loggedInUser = null
        currentUsernameFlow.value = null
        loginError = null
        registerError = null
        registerSuccess = false
        navigateTo(GameScreen.Login)
    }

    fun clearErrors() {
        loginError = null
        registerError = null
    }

    fun updateProfile(displayName: String, passwordHash: String) {
        val user = loggedInUser ?: return
        val dClean = displayName.trim()
        val pClean = passwordHash.trim()

        if (dClean.isBlank() || pClean.isBlank()) {
            profileError = "Semua kolom harus diisi!"
            return
        }
        if (pClean.length < 4) {
            profileError = "Password minimal 4 karakter!"
            return
        }

        viewModelScope.launch {
            try {
                val updatedUser = User(user.username, pClean, dClean, user.role)
                repository.updateUser(updatedUser)
                loggedInUser = updatedUser
                profileSuccess = true
                profileError = null
            } catch (e: Exception) {
                profileError = "Gagal memperbarui profil: ${e.message}"
            }
        }
    }

    fun clearProfileStatus() {
        profileError = null
        profileSuccess = false
    }

    // --- Owner / Teacher Dashboard State and Functions ---
    var allUsersList by mutableStateOf<List<User>>(emptyList())
        private set

    fun loadAllUsers() {
        viewModelScope.launch {
            try {
                allUsersList = repository.getAllUsers()
            } catch (e: Exception) {
                // handle error silently
            }
        }
    }

    fun deleteUserByOwner(username: String) {
        viewModelScope.launch {
            try {
                repository.deleteUser(username)
                loadAllUsers() // Refresh the user list
            } catch (e: Exception) {
                // handle error silently
            }
        }
    }

    fun updateUserRole(user: User, newRole: String) {
        viewModelScope.launch {
            try {
                val updated = User(user.username, user.passwordHash, user.displayName, newRole)
                repository.updateUser(updated)
                loadAllUsers() // Refresh the user list
                if (loggedInUser?.username == user.username) {
                    loggedInUser = updated
                }
            } catch (e: Exception) {
                // handle error silently
            }
        }
    }

    fun unlockAllLevelsForUser(username: String) {
        viewModelScope.launch {
            try {
                repository.unlockAllLevels(username)
                loadAllUsers()
            } catch (e: Exception) {
                // handle error silently
            }
        }
    }


    // Navigation Methods
    fun navigateTo(screen: GameScreen) {
        currentScreen = screen
        if (screen == GameScreen.MainMenu) {
            stopTimer()
        }
    }

    fun selectLevel(level: LevelData) {
        selectedLevel = level
        navigateTo(GameScreen.LevelInstructions)
    }

    fun startLevel(levelId: Int) {
        currentScore = 0
        lastScoreAdded = 0
        currentLives = 3
        activeQuestionIndex = 0
        hintText = ""
        showCorrectAnimation = false
        showIncorrectAnimation = false
        lastSelectedOptionIndex = -1

        when (levelId) {
            1 -> loadQuizLevel1(0)
            2 -> loadQuizLevel2(0)
            3 -> loadQuizLevel3(0)
            4 -> startLightningLevel()
            5 -> loadQuizLevel5(0)
            6 -> loadQuizLevel6(0)
            7 -> loadQuizLevel7(0)
            8 -> loadQuizLevel8(0)
        }
        navigateTo(GameScreen.PlayLevel)
    }

    // Reset Progress
    fun resetAllProgress() {
        val username = loggedInUser?.username ?: return
        viewModelScope.launch {
            repository.resetAllProgress(username)
        }
    }

    // --- LEVEL 1 LOAD & LOGIC ---
    private fun loadQuizLevel1(index: Int) {
        if (index < GameQuestions.level1Quizzes.size) {
            activeQuestionIndex = index
            currentQuiz = GameQuestions.level1Quizzes[index]
            lastSelectedOptionIndex = -1
            hintText = ""
        } else {
            finishLevel()
        }
    }

    fun submitAnswerLevel1(optionIndex: Int) {
        val quiz = currentQuiz ?: return
        lastSelectedOptionIndex = optionIndex

        if (optionIndex == quiz.correctIndex) {
            // Correct Answer
            currentScore += 20
            lastScoreAdded = 20
            showCorrectAnimation = true
            SoundManager.playCorrect()
            viewModelScope.launch {
                delay(1200)
                showCorrectAnimation = false
                loadQuizLevel1(activeQuestionIndex + 1)
            }
        } else {
            // Incorrect Answer
            currentLives--
            showIncorrectAnimation = true
            hintText = quiz.hint
            SoundManager.playIncorrect()
            viewModelScope.launch {
                delay(1200)
                showIncorrectAnimation = false
                if (currentLives <= 0) {
                    navigateTo(GameScreen.GameOverScreen)
                }
            }
        }
    }

    // --- LEVEL 2 LOAD & LOGIC ---
    private fun loadQuizLevel2(index: Int) {
        if (index < GameQuestions.level2Quizzes.size) {
            activeQuestionIndex = index
            currentQuiz = GameQuestions.level2Quizzes[index]
            lastSelectedOptionIndex = -1
            hintText = ""
        } else {
            finishLevel()
        }
    }

    fun submitAnswerLevel2(optionIndex: Int) {
        val quiz = currentQuiz ?: return
        lastSelectedOptionIndex = optionIndex

        if (optionIndex == quiz.correctIndex) {
            currentScore += 20
            lastScoreAdded = 20
            showCorrectAnimation = true
            SoundManager.playCorrect()
            viewModelScope.launch {
                delay(1200)
                showCorrectAnimation = false
                loadQuizLevel2(activeQuestionIndex + 1)
            }
        } else {
            currentLives--
            showIncorrectAnimation = true
            hintText = quiz.hint
            SoundManager.playIncorrect()
            viewModelScope.launch {
                delay(1200)
                showIncorrectAnimation = false
                if (currentLives <= 0) {
                    navigateTo(GameScreen.GameOverScreen)
                }
            }
        }
    }

    // --- LEVEL 3 LOAD & LOGIC ---
    private fun loadQuizLevel3(index: Int) {
        if (index < GameQuestions.level3Quizzes.size) {
            activeQuestionIndex = index
            level3Quiz = GameQuestions.level3Quizzes[index]
            level3PoppedBubbles = emptyList()
            hintText = ""
        } else {
            finishLevel()
        }
    }

    fun popBubble(bubbleWord: String) {
        val quiz = level3Quiz ?: return
        val currentChainIndex = level3PoppedBubbles.size
        val expectedWord = quiz.correctChain.getOrNull(currentChainIndex)

        if (bubbleWord == expectedWord) {
            // Correct chain piece popped!
            level3PoppedBubbles = level3PoppedBubbles + bubbleWord
            currentScore += 10
            lastScoreAdded = 10
            showCorrectAnimation = true
            SoundManager.playCorrect()
            viewModelScope.launch {
                delay(600)
                showCorrectAnimation = false
                // Check if the entire chain is complete
                if (level3PoppedBubbles.size == quiz.correctChain.size) {
                    currentScore += 10 // Bonus for completed chain
                    lastScoreAdded = 20 // Show total reward for chain completion!
                    delay(500)
                    loadQuizLevel3(activeQuestionIndex + 1)
                }
            }
        } else {
            // Wrong bubble popped!
            currentLives--
            showIncorrectAnimation = true
            SoundManager.playIncorrect()
            val startingSyllable = if (level3PoppedBubbles.isEmpty()) {
                quiz.startWord.takeLast(2).uppercase()
            } else {
                level3PoppedBubbles.last().takeLast(2).uppercase()
            }
            hintText = "Aduh! Cari kata yang berawalan: $startingSyllable"
            viewModelScope.launch {
                delay(1200)
                showIncorrectAnimation = false
                if (currentLives <= 0) {
                    navigateTo(GameScreen.GameOverScreen)
                }
            }
        }
    }

    // --- LEVEL 4 LOAD & LOGIC (Endless Lightning) ---
    private fun startLightningLevel() {
        lightningTimerSeconds = 30
        currentScore = 0
        currentLightningWord = GameQuestions.level4Words.random()
        generateLightningOptions()
        startTimer()
    }

    private fun generateLightningOptions() {
        val lastTwoLetters = currentLightningWord.takeLast(2).uppercase()
        // Find a word from dictionary that starts with lastTwoLetters
        val matchingWord = GameQuestions.level4Words.filter {
            it.startsWith(lastTwoLetters, ignoreCase = true) && it != currentLightningWord
        }.randomOrNull() ?: GameQuestions.level4Words.random()

        // Gather 5 random words as decoys
        val otherOptions = GameQuestions.level4Words.filter {
            !it.startsWith(lastTwoLetters, ignoreCase = true) && it != currentLightningWord
        }.shuffled().take(5)

        // Combine and shuffle
        lightningOptions = (otherOptions + matchingWord).shuffled()
    }

    fun selectLightningWord(selectedWord: String) {
        val lastTwoLetters = currentLightningWord.takeLast(2).uppercase()
        if (selectedWord.startsWith(lastTwoLetters, ignoreCase = true)) {
            // Correct connection!
            currentScore += 15
            lastScoreAdded = 15
            lightningTimerSeconds += 3 // time bonus!
            currentLightningWord = selectedWord
            showCorrectAnimation = true
            SoundManager.playCorrect()
            generateLightningOptions()
            viewModelScope.launch {
                delay(500)
                showCorrectAnimation = false
            }
        } else {
            // Mistake
            lightningTimerSeconds = maxOf(0, lightningTimerSeconds - 3) // time penalty!
            showIncorrectAnimation = true
            SoundManager.playIncorrect()
            viewModelScope.launch {
                delay(500)
                showIncorrectAnimation = false
            }
        }
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (lightningTimerSeconds > 0) {
                delay(1000)
                lightningTimerSeconds--
            }
            // Timer finished
            finishLevel()
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    // --- FINISH & SCORE CALCULATION ---
    private fun finishLevel() {
        stopTimer()
        val level = selectedLevel ?: return

        earnedStars = when (level.id) {
            1, 2, 3, 5, 6, 7, 8 -> {
                when (currentLives) {
                    3 -> 3
                    2 -> 2
                    1 -> 1
                    else -> 0
                }
            }
            4 -> {
                // For level 4, stars are based on score achieved
                if (currentScore >= 120) 3
                else if (currentScore >= 60) 2
                else if (currentScore >= 30) 1
                else 0
            }
            else -> 1
        }

        val username = loggedInUser?.username ?: ""
        viewModelScope.launch {
            repository.updateProgress(username, level.id, earnedStars, currentScore)
        }
        navigateTo(GameScreen.VictoryScreen)
    }

    // --- LEVEL 5 LOAD & LOGIC (3-Suku Kata) ---
    private fun loadQuizLevel5(index: Int) {
        if (index < GameQuestions.level5Quizzes.size) {
            activeQuestionIndex = index
            currentQuiz = GameQuestions.level5Quizzes[index]
            lastSelectedOptionIndex = -1
            hintText = ""
        } else {
            finishLevel()
        }
    }

    fun submitAnswerLevel5(optionIndex: Int) {
        val quiz = currentQuiz ?: return
        lastSelectedOptionIndex = optionIndex

        if (optionIndex == quiz.correctIndex) {
            currentScore += 20
            lastScoreAdded = 20
            showCorrectAnimation = true
            SoundManager.playCorrect()
            viewModelScope.launch {
                delay(1200)
                showCorrectAnimation = false
                loadQuizLevel5(activeQuestionIndex + 1)
            }
        } else {
            currentLives--
            showIncorrectAnimation = true
            hintText = quiz.hint
            SoundManager.playIncorrect()
            viewModelScope.launch {
                delay(1200)
                showIncorrectAnimation = false
                if (currentLives <= 0) {
                    navigateTo(GameScreen.GameOverScreen)
                }
            }
        }
    }

    // --- LEVEL 6 LOAD & LOGIC (Lengkapi 3-4 Suku Kata) ---
    private fun loadQuizLevel6(index: Int) {
        if (index < GameQuestions.level6Quizzes.size) {
            activeQuestionIndex = index
            currentQuiz = GameQuestions.level6Quizzes[index]
            lastSelectedOptionIndex = -1
            hintText = ""
        } else {
            finishLevel()
        }
    }

    fun submitAnswerLevel6(optionIndex: Int) {
        val quiz = currentQuiz ?: return
        lastSelectedOptionIndex = optionIndex

        if (optionIndex == quiz.correctIndex) {
            currentScore += 20
            lastScoreAdded = 20
            showCorrectAnimation = true
            SoundManager.playCorrect()
            viewModelScope.launch {
                delay(1200)
                showCorrectAnimation = false
                loadQuizLevel6(activeQuestionIndex + 1)
            }
        } else {
            currentLives--
            showIncorrectAnimation = true
            hintText = quiz.hint
            SoundManager.playIncorrect()
            viewModelScope.launch {
                delay(1200)
                showIncorrectAnimation = false
                if (currentLives <= 0) {
                    navigateTo(GameScreen.GameOverScreen)
                }
            }
        }
    }

    // --- LEVEL 7 LOAD & LOGIC (Lengkapi Kalimat) ---
    private fun loadQuizLevel7(index: Int) {
        if (index < GameQuestions.level7Quizzes.size) {
            activeQuestionIndex = index
            currentSentenceQuiz = GameQuestions.level7Quizzes[index]
            lastSelectedOptionIndex = -1
            hintText = ""
        } else {
            finishLevel()
        }
    }

    fun submitAnswerLevel7(optionIndex: Int) {
        val quiz = currentSentenceQuiz ?: return
        lastSelectedOptionIndex = optionIndex

        if (optionIndex == quiz.correctIndex) {
            currentScore += 20
            lastScoreAdded = 20
            showCorrectAnimation = true
            SoundManager.playCorrect()
            viewModelScope.launch {
                delay(1200)
                showCorrectAnimation = false
                loadQuizLevel7(activeQuestionIndex + 1)
            }
        } else {
            currentLives--
            showIncorrectAnimation = true
            hintText = quiz.hint
            SoundManager.playIncorrect()
            viewModelScope.launch {
                delay(1200)
                showIncorrectAnimation = false
                if (currentLives <= 0) {
                    navigateTo(GameScreen.GameOverScreen)
                }
            }
        }
    }

    // --- LEVEL 8 LOAD & LOGIC (Soal Cerita Pendek) ---
    private fun loadQuizLevel8(index: Int) {
        if (index < GameQuestions.level8Quizzes.size) {
            activeQuestionIndex = index
            currentStoryQuiz = GameQuestions.level8Quizzes[index]
            lastSelectedOptionIndex = -1
            hintText = ""
        } else {
            finishLevel()
        }
    }

    fun submitAnswerLevel8(optionIndex: Int) {
        val quiz = currentStoryQuiz ?: return
        lastSelectedOptionIndex = optionIndex

        if (optionIndex == quiz.correctIndex) {
            currentScore += 20
            lastScoreAdded = 20
            showCorrectAnimation = true
            SoundManager.playCorrect()
            viewModelScope.launch {
                delay(1200)
                showCorrectAnimation = false
                loadQuizLevel8(activeQuestionIndex + 1)
            }
        } else {
            currentLives--
            showIncorrectAnimation = true
            hintText = quiz.hint
            SoundManager.playIncorrect()
            viewModelScope.launch {
                delay(1200)
                showIncorrectAnimation = false
                if (currentLives <= 0) {
                    navigateTo(GameScreen.GameOverScreen)
                }
            }
        }
    }

    fun submitEssayAnswer(answer: String) {
        val level = selectedLevel ?: return
        val cleanInput = answer.trim()
        if (cleanInput.isBlank()) return

        var isCorrect = false
        var expectedAnswerText = ""
        var hint = ""

        when (level.id) {
            1 -> {
                val quiz = currentQuiz ?: return
                expectedAnswerText = quiz.options[quiz.correctIndex].word
                hint = quiz.hint
                isCorrect = checkEssayAnswer(cleanInput, expectedAnswerText)
            }
            2 -> {
                val quiz = currentQuiz ?: return
                expectedAnswerText = quiz.options[quiz.correctIndex].word
                hint = quiz.hint
                isCorrect = checkEssayAnswer(cleanInput, expectedAnswerText)
            }
            5 -> {
                val quiz = currentQuiz ?: return
                expectedAnswerText = quiz.options[quiz.correctIndex].word
                hint = quiz.hint
                isCorrect = checkEssayAnswer(cleanInput, expectedAnswerText)
            }
            6 -> {
                val quiz = currentQuiz ?: return
                expectedAnswerText = quiz.options[quiz.correctIndex].word
                hint = quiz.hint
                isCorrect = checkEssayAnswer(cleanInput, expectedAnswerText)
            }
            7 -> {
                val quiz = currentSentenceQuiz ?: return
                expectedAnswerText = quiz.options[quiz.correctIndex]
                hint = quiz.hint
                isCorrect = checkEssayAnswer(cleanInput, expectedAnswerText)
            }
            8 -> {
                val quiz = currentStoryQuiz ?: return
                expectedAnswerText = quiz.options[quiz.correctIndex]
                hint = quiz.hint
                isCorrect = checkEssayAnswer(cleanInput, expectedAnswerText)
            }
            else -> return
        }

        if (isCorrect) {
            currentScore += 25 // 25 points for essay challenge (increased difficulty reward!)
            lastScoreAdded = 25
            showCorrectAnimation = true
            SoundManager.playCorrect()
            viewModelScope.launch {
                delay(1200)
                showCorrectAnimation = false
                val nextIndex = activeQuestionIndex + 1
                when (level.id) {
                    1 -> loadQuizLevel1(nextIndex)
                    2 -> loadQuizLevel2(nextIndex)
                    5 -> loadQuizLevel5(nextIndex)
                    6 -> loadQuizLevel6(nextIndex)
                    7 -> loadQuizLevel7(nextIndex)
                    8 -> loadQuizLevel8(nextIndex)
                }
            }
        } else {
            currentLives--
            showIncorrectAnimation = true
            hintText = hint
            SoundManager.playIncorrect()
            viewModelScope.launch {
                delay(1200)
                showIncorrectAnimation = false
                if (currentLives <= 0) {
                    navigateTo(GameScreen.GameOverScreen)
                }
            }
        }
    }

    private fun checkEssayAnswer(input: String, correctRaw: String): Boolean {
        val cleanIn = input.trim().lowercase()
        val cleanCorr = correctRaw.trim().lowercase()
        if (cleanIn == cleanCorr) return true

        // If exact match of correct word inside brackets/parentheses, e.g. "RI (LARI)" -> "ri" or "lari"
        if (cleanCorr.contains("(") && cleanCorr.contains(")")) {
            val part1 = cleanCorr.substringBefore("(").trim()
            val part2 = cleanCorr.substringAfter("(").substringBefore(")").trim()
            if (cleanIn == part1 || cleanIn == part2 || cleanIn == "$part1 $part2") return true
        }

        // Substring checks for story/sentence answers (e.g. "di lapangan" vs "lapangan")
        if (cleanCorr.contains(cleanIn) && cleanIn.length >= 3) return true
        if (cleanIn.contains(cleanCorr) && cleanCorr.length >= 3) return true

        return false
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
