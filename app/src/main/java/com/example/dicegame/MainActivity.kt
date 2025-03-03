package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import com.example.dicegame.ui.theme.DiceGameTheme
import kotlin.random.Random
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceGameTheme {
                DiceGameScreen()
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiceGameScreen() {
    // State variables for game logic and UI
    var currentPlayer by remember { mutableIntStateOf(1) } // Tracks the current player (1 or 2)
    var player1Score by remember { mutableIntStateOf(0) } // Player 1's score
    var player2Score by remember { mutableIntStateOf(0) } // Player 2's score
    var jackpot by remember { mutableIntStateOf(5) } // Jackpot points, starts at 5
    var message by remember { mutableStateOf("") } // Message displayed below the die (e.g., problem to solve)
    var num1 by remember { mutableIntStateOf(0) } // First number for the math problem
    var num2 by remember { mutableIntStateOf(0) } // Second number for the math problem
    var correctAnswer by remember { mutableIntStateOf(0) } // Correct answer to the problem
    var playerAnswer by remember { mutableStateOf(TextFieldValue("")) } // Player's input answer
    var diceRoll by remember { mutableIntStateOf(1) } // Current dice roll (1 to 6)
    var isRollAgainActive by remember { mutableStateOf(false) } // Tracks if "Roll Again" is active (dice roll 4)
    var problemSolved by remember { mutableStateOf(true) } // Tracks if the current problem is solved
    var errorMessage by remember { mutableStateOf("") } // Error message for invalid input
    var isWinner by remember { mutableStateOf(false) } // Tracks if there’s a winner
    var winnerMessage by remember { mutableStateOf("") } // Winner message (e.g., "Player 1 Wins!")
    var triggerDiceRoll by remember { mutableStateOf(false) } // Triggers dice roll animation
    var jackpotProblemType by remember { mutableIntStateOf(1) } // Tracks jackpot problem type (1=addition, 2=subtraction, 3=multiplication)
    var pendingTurnSwitch by remember { mutableStateOf(false) } // Tracks if a turn switch is pending (dice roll 5)

    // Animation state for dice rotation
    val rotationState = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Handle dice roll animation when triggerDiceRoll changes
    LaunchedEffect(triggerDiceRoll) {
        if (triggerDiceRoll) {
            rotationState.animateTo(
                targetValue = rotationState.value + 720f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            )
            triggerDiceRoll = false
        }
    }

    // Handle turn switch after losing a turn (dice roll 5)
    LaunchedEffect(pendingTurnSwitch) {
        if (pendingTurnSwitch) {
            coroutineScope.launch {
                problemSolved = true
                pendingTurnSwitch = false
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp) // Increased spacing for better readability
            ) {
                // Header: App Title
                Text(
                    text = "Dice Game",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Player Scores and Jackpot Section
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Player 1: $player1Score points",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Player 2: $player2Score points",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Text(
                    text = "Jackpot: $jackpot points",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Current Player
                Text(
                    text = "Current Player: P$currentPlayer",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Dice Image with Rotation Animation
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = getDiceImage(diceRoll)),
                        contentDescription = "Dice Roll",
                        modifier = Modifier
                            .size(100.dp)
                            .graphicsLayer(rotationZ = rotationState.value)
                    )
                }

                // Roll Dice Button (Hidden when there's a winner)
                if (!isWinner) {
                    Button(
                        onClick = {
                            if (!triggerDiceRoll) {
                                triggerDiceRoll = true
                                problemSolved = false
                                errorMessage = ""
                                isRollAgainActive = false

                                coroutineScope.launch {
                                    diceRoll = Random.nextInt(1, 7)
                                    when (diceRoll) {
                                        1 -> {
                                            num1 = Random.nextInt(0, 100)
                                            num2 = Random.nextInt(0, 100)
                                            correctAnswer = num1 + num2
                                            message = "Addition! Solve: $num1 + $num2 (1 point)"
                                        }
                                        2 -> {
                                            num1 = Random.nextInt(0, 100)
                                            num2 = Random.nextInt(0, 100)
                                            correctAnswer = num1 - num2
                                            message = "Subtraction! Solve: $num1 - $num2 (2 points)"
                                        }
                                        3 -> {
                                            num1 = Random.nextInt(0, 21)
                                            num2 = Random.nextInt(0, 21)
                                            correctAnswer = num1 * num2
                                            message = "Multiplication! Solve: $num1 × $num2 (3 points)"
                                        }
                                        4 -> {
                                            message = "Roll Again for Double Points! Press 'Roll Again' to proceed."
                                            isRollAgainActive = true
                                        }
                                        5 -> {
                                            message = "Lose a turn! Switching to Player ${if (currentPlayer == 1) 2 else 1}."
                                            currentPlayer = if (currentPlayer == 1) 2 else 1
                                            pendingTurnSwitch = true
                                        }
                                        6 -> {
                                            jackpotProblemType = Random.nextInt(1, 4)
                                            when (jackpotProblemType) {
                                                1 -> {
                                                    num1 = Random.nextInt(0, 100)
                                                    num2 = Random.nextInt(0, 100)
                                                    correctAnswer = num1 + num2
                                                    message = "Jackpot! Addition - Solve: $num1 + $num2 ($jackpot points)"
                                                }
                                                2 -> {
                                                    num1 = Random.nextInt(0, 100)
                                                    num2 = Random.nextInt(0, 100)
                                                    correctAnswer = num1 - num2
                                                    message = "Jackpot! Subtraction - Solve: $num1 - $num2 ($jackpot points)"
                                                }
                                                3 -> {
                                                    num1 = Random.nextInt(0, 21)
                                                    num2 = Random.nextInt(0, 21)
                                                    correctAnswer = num1 * num2
                                                    message = "Jackpot! Multiplication - Solve: $num1 × $num2 ($jackpot points)"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        enabled = problemSolved && !triggerDiceRoll && !pendingTurnSwitch,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Text(
                            text = "Roll Dice",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Roll Again Button
                if (diceRoll == 4 && isRollAgainActive) {
                    Button(
                        onClick = {
                            diceRoll = Random.nextInt(1, 4)
                            isRollAgainActive = false
                            when (diceRoll) {
                                1 -> {
                                    num1 = Random.nextInt(0, 100)
                                    num2 = Random.nextInt(0, 100)
                                    correctAnswer = num1 + num2
                                    message = "Roll Again: Addition! Solve: $num1 + $num2 (Double Points: 2)"
                                }
                                2 -> {
                                    num1 = Random.nextInt(0, 100)
                                    num2 = Random.nextInt(0, 100)
                                    correctAnswer = num1 - num2
                                    message = "Roll Again: Subtraction! Solve: $num1 - $num2 (Double Points: 4)"
                                }
                                3 -> {
                                    num1 = Random.nextInt(0, 21)
                                    num2 = Random.nextInt(0, 21)
                                    correctAnswer = num1 * num2
                                    message = "Roll Again: Multiplication! Solve: $num1 × $num2 (Double Points: 6)"
                                }
                            }
                        },
                        enabled = !problemSolved,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Text(
                            text = "Roll Again",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Display Message (problem to solve or game status)
                AnimatedContent(
                    targetState = message,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    }
                ) { targetMessage ->
                    Text(
                        text = targetMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Input Field for Solving Problems
                if (message.contains("Solve:") && !problemSolved) {
                    TextField(
                        value = playerAnswer,
                        onValueChange = { playerAnswer = it },
                        label = { Text("Your Answer") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        isError = errorMessage.isNotEmpty(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        shape = MaterialTheme.shapes.small,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            errorContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    Button(
                        onClick = {
                            val answer = playerAnswer.text.toIntOrNull()
                            if (answer == null) {
                                errorMessage = "Please enter a valid number!"
                                return@Button
                            }
                            errorMessage = ""

                            if (answer == correctAnswer) {
                                if (message.contains("Roll Again")) {
                                    when (diceRoll) {
                                        1 -> {
                                            message = "Correct! 2 Double Points awarded."
                                            if (currentPlayer == 1) player1Score += 2
                                            else player2Score += 2
                                        }
                                        2 -> {
                                            message = "Correct! 4 Double Points awarded."
                                            if (currentPlayer == 1) player1Score += 4
                                            else player2Score += 4
                                        }
                                        3 -> {
                                            message = "Correct! 6 Double Points awarded."
                                            if (currentPlayer == 1) player1Score += 6
                                            else player2Score += 6
                                        }
                                    }
                                } else {
                                    when (diceRoll) {
                                        1 -> {
                                            message = "Correct! Points awarded."
                                            if (currentPlayer == 1) player1Score += 1
                                            else player2Score += 1
                                        }
                                        2 -> {
                                            message = "Correct! Points awarded."
                                            if (currentPlayer == 1) player1Score += 2
                                            else player2Score += 2
                                        }
                                        3 -> {
                                            message = "Correct! Points awarded."
                                            if (currentPlayer == 1) player1Score += 3
                                            else player2Score += 3
                                        }
                                        6 -> {
                                            message = "Correct! Jackpot won."
                                            if (currentPlayer == 1) player1Score += jackpot
                                            else player2Score += jackpot
                                            jackpot = 5
                                        }
                                    }
                                }
                            } else {
                                if (message.contains("Roll Again")) {
                                    when (diceRoll) {
                                        1 -> {
                                            jackpot += 2
                                            message = "Wrong! 2 points added to the jackpot."
                                        }
                                        2 -> {
                                            jackpot += 4
                                            message = "Wrong! 4 points added to the jackpot."
                                        }
                                        3 -> {
                                            jackpot += 6
                                            message = "Wrong! 6 points added to the jackpot."
                                        }
                                    }
                                } else {
                                    when (diceRoll) {
                                        1 -> {
                                            jackpot += 1
                                            message = "Wrong! 1 point added to the jackpot."
                                        }
                                        2 -> {
                                            jackpot += 2
                                            message = "Wrong! 2 points added to the jackpot."
                                        }
                                        3 -> {
                                            jackpot += 3
                                            message = "Wrong! 3 points added to the jackpot."
                                        }
                                        6 -> {
                                            when (jackpotProblemType) {
                                                1 -> {
                                                    jackpot += 1
                                                    message = "Wrong! 1 point added to the jackpot (Addition)."
                                                }
                                                2 -> {
                                                    jackpot += 2
                                                    message = "Wrong! 2 points added to the jackpot (Subtraction)."
                                                }
                                                3 -> {
                                                    jackpot += 3
                                                    message = "Wrong! 3 points added to the jackpot (Multiplication)."
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            playerAnswer = TextFieldValue("")
                            problemSolved = true
                            focusManager.clearFocus()

                            if (player1Score >= 20 || player2Score >= 20) {
                                isWinner = true
                                winnerMessage = if (player1Score >= 20) "Player 1 Wins!" else "Player 2 Wins!"
                            } else {
                                currentPlayer = if (currentPlayer == 1) 2 else 1
                            }
                        },
                        enabled = !problemSolved,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Text(
                            text = "Guess",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Play Again Button (Visible only when there's a winner)
                if (isWinner) {
                    Button(
                        onClick = {
                            player1Score = 0
                            player2Score = 0
                            jackpot = 5
                            currentPlayer = 1
                            isWinner = false
                            winnerMessage = ""
                            message = ""
                            playerAnswer = TextFieldValue("")
                            problemSolved = true
                            coroutineScope.launch {
                                rotationState.snapTo(0f)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Text(
                            text = "Play Again",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = winnerMessage,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Add bottom padding to ensure content isn’t cut off
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

/**
 * Maps the dice roll value to the corresponding die image resource.
 * @param roll The dice roll value (1 to 6).
 * @return The resource ID of the die image.
 */
fun getDiceImage(roll: Int): Int {
    return when (roll) {
        1 -> R.drawable.die1
        2 -> R.drawable.die2
        3 -> R.drawable.die3
        4 -> R.drawable.die4
        5 -> R.drawable.die5
        else -> R.drawable.die6
    }
}

@Preview(showBackground = true)
@Composable
fun DiceGamePreview() {
    DiceGameTheme {
        DiceGameScreen()
    }
}