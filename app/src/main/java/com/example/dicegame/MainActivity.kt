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
import androidx.compose.ui.text.font.FontWeight
import kotlin.random.Random
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
    var currentPlayer by remember { mutableIntStateOf(1) }
    var player1Score by remember { mutableIntStateOf(0) }
    var player2Score by remember { mutableIntStateOf(0) }
    var jackpot by remember { mutableIntStateOf(5) }
    var message by remember { mutableStateOf("") }
    var num1 by remember { mutableIntStateOf(0) }
    var num2 by remember { mutableIntStateOf(0) }
    var correctAnswer by remember { mutableIntStateOf(0) }
    var playerAnswer by remember { mutableStateOf(TextFieldValue("")) }
    var diceRoll by remember { mutableIntStateOf(1) }
    var isRollAgainActive by remember { mutableStateOf(false) }
    var problemSolved by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var isWinner by remember { mutableStateOf(false) }
    var winnerMessage by remember { mutableStateOf("") }
    var triggerDiceRoll by remember { mutableStateOf(false) }
    var jackpotProblemType by remember { mutableIntStateOf(1) }
    var pendingTurnSwitch by remember { mutableStateOf(false) } // Track if a turn switch is pending

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
            // Small delay to ensure the UI updates before allowing the next roll
            coroutineScope.launch {
                // Reset the pending turn switch and allow the next roll
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Dice Game",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                // Player Scores and Jackpot
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Player 1: $player1Score points",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                    Text(
                        text = "Player 2: $player2Score points",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
                Text(
                    text = "Jackpot: $jackpot points",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                )

                // Current Player
                Text(
                    text = "Current Player: P$currentPlayer",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
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
                                            message = "Addition! Solve: $num1 + $num2"
                                        }
                                        2 -> {
                                            num1 = Random.nextInt(0, 100)
                                            num2 = Random.nextInt(0, 100)
                                            correctAnswer = num1 - num2
                                            message = "Subtraction! Solve: $num1 - $num2"
                                        }
                                        3 -> {
                                            num1 = Random.nextInt(0, 20)
                                            num2 = Random.nextInt(0, 20)
                                            correctAnswer = num1 * num2
                                            message = "Multiplication! Solve: $num1 × $num2"
                                        }
                                        4 -> {
                                            message = "Roll Again for Double Points! Press 'Roll Again' to proceed."
                                            isRollAgainActive = true
                                        }
                                        5 -> {
                                            message = "Lose a turn! Switching to Player ${if (currentPlayer == 1) 2 else 1}."
                                            currentPlayer = if (currentPlayer == 1) 2 else 1
                                            pendingTurnSwitch = true // Trigger turn switch
                                        }
                                        6 -> {
                                            jackpotProblemType = Random.nextInt(1, 4)
                                            when (jackpotProblemType) {
                                                1 -> {
                                                    num1 = Random.nextInt(0, 100)
                                                    num2 = Random.nextInt(0, 100)
                                                    correctAnswer = num1 + num2
                                                    message = "Jackpot! Addition - Solve: $num1 + $num2"
                                                }
                                                2 -> {
                                                    num1 = Random.nextInt(0, 100)
                                                    num2 = Random.nextInt(0, 100)
                                                    correctAnswer = num1 - num2
                                                    message = "Jackpot! Subtraction - Solve: $num1 - $num2"
                                                }
                                                3 -> {
                                                    num1 = Random.nextInt(0, 20)
                                                    num2 = Random.nextInt(0, 20)
                                                    correctAnswer = num1 * num2
                                                    message = "Jackpot! Multiplication - Solve: $num1 × $num2"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        enabled = problemSolved && !triggerDiceRoll && !pendingTurnSwitch // Disable until turn switch completes
                    ) {
                        Text("Roll Dice")
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
                                    num1 = Random.nextInt(0, 20)
                                    num2 = Random.nextInt(0, 20)
                                    correctAnswer = num1 * num2
                                    message = "Roll Again: Multiplication! Solve: $num1 × $num2 (Double Points: 6)"
                                }
                            }
                        },
                        enabled = !problemSolved
                    ) {
                        Text("Roll Again")
                    }
                }

                // Display Message
                AnimatedContent(
                    targetState = message,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    }
                ) {
                    Text(it)
                }

                // Combined Input Field for All Problems
                if (message.contains("Solve:") && !problemSolved) {
                    TextField(
                        value = playerAnswer,
                        onValueChange = { playerAnswer = it },
                        label = { Text("Your Answer") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorMessage.isNotEmpty(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        )
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
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
                        enabled = !problemSolved
                    ) {
                        Text("Guess")
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
                        }
                    ) {
                        Text("Play Again")
                    }

                    Text(
                        text = winnerMessage,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    )
}

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