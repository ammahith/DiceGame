package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dicegame.ui.theme.DiceGameTheme
import androidx.compose.ui.text.font.FontWeight

import kotlin.random.Random

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
    var isDiceRolling by remember { mutableStateOf(false) }
    var problemSolved by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var isWinner by remember { mutableStateOf(false) }
    var winnerMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
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

            // Dice Image
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = getDiceImage(diceRoll)),
                    contentDescription = "Dice Roll",
                    modifier = Modifier
                        .size(100.dp)
                )
            }

// Roll Dice Button
            Button(
                onClick = {
                    problemSolved = false
                    errorMessage = ""
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
                            message = "Roll Again! Press the 'Roll Again' button to proceed."
                            // Enable the Roll Again state
                            problemSolved = false // Ensure Roll Again logic triggers
                        }

                        5 -> {
                            message =
                                "Lose a turn! Switching to Player ${if (currentPlayer == 1) 2 else 1}."
                            currentPlayer = if (currentPlayer == 1) 2 else 1
                            problemSolved = true
                        }

                        6 -> {
                            num1 = Random.nextInt(0, 100)
                            num2 = Random.nextInt(0, 100)
                            correctAnswer = num1 + num2
                            message = "Jackpot! Solve: $num1 + $num2"
                        }
                    }
                },
                enabled = problemSolved
            ) {
                Text("Roll Dice")
            }

// Roll Again Button (Only enabled when diceRoll = 4)
            if (diceRoll == 4) {
                Button(
                    onClick = {
                        diceRoll = Random.nextInt(1, 4) // Roll Again restricted to 1, 2, or 3
                        when (diceRoll) {
                            1 -> { // Addition
                                num1 = Random.nextInt(0, 100)
                                num2 = Random.nextInt(0, 100)
                                correctAnswer = num1 + num2
                                message = "Roll Again: Addition! Solve: $num1 + $num2"
                            }

                            2 -> { // Subtraction
                                num1 = Random.nextInt(0, 100)
                                num2 = Random.nextInt(0, 100)
                                correctAnswer = num1 - num2
                                message = "Roll Again: Subtraction! Solve: $num1 - $num2"
                            }

                            3 -> { // Multiplication
                                num1 = Random.nextInt(0, 20)
                                num2 = Random.nextInt(0, 20)
                                correctAnswer = num1 * num2
                                message = "Roll Again: Multiplication! Solve: $num1 × $num2"
                            }
                        }
                    },
                    enabled = !problemSolved // Only enable Roll Again if a problem isn't solved yet
                ) {
                    Text("Roll Again")
                }
            }

// Display Message
            AnimatedContent(
                targetState = message,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                }
            ) {
                Text(it)
            }

// Player Input and Guess Button
            if (diceRoll in 1..4 || diceRoll == 6) {
                TextField(
                    value = playerAnswer,
                    onValueChange = { playerAnswer = it },
                    label = { Text("Your Answer") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage.isNotEmpty()
                )

                // Display Error Message
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
                            when (diceRoll) {
                                4 -> {
                                    message = "Correct! Double Points awarded."
                                    if (currentPlayer == 1) player1Score += diceRoll * 2
                                    else player2Score += diceRoll * 2
                                }

                                6 -> {
                                    message = "Correct! Jackpot won."
                                    if (currentPlayer == 1) player1Score += jackpot
                                    else player2Score += jackpot
                                    jackpot = 5 // Reset the jackpot
                                }

                                else -> {
                                    message = "Correct! Points awarded."
                                    if (currentPlayer == 1) player1Score += diceRoll
                                    else player2Score += diceRoll
                                }
                            }
                        } else {
                            // Adjust jackpot based on the operation
                            when (diceRoll) {
                                1 -> { // Addition
                                    jackpot += 1
                                    message = "Wrong! 1 point added to the jackpot."
                                }

                                2 -> { // Subtraction
                                    jackpot += 2
                                    message = "Wrong! 2 points added to the jackpot."
                                }

                                3 -> { // Multiplication
                                    jackpot += 3
                                    message = "Wrong! 3 points added to the jackpot."
                                }

                                4 -> { // Roll Again (Double Points)
                                    jackpot += 1 // Default to addition logic for Roll Again
                                    message = "Wrong! 1 point added to the jackpot."
                                }

                                6 -> { // Jackpot
                                    // Determine the operation and adjust jackpot accordingly
                                    when {
                                        correctAnswer == num1 + num2 -> {
                                            jackpot += 1
                                            message =
                                                "Wrong! 1 point added to the jackpot (Addition)."
                                        }

                                        correctAnswer == num1 - num2 -> {
                                            jackpot += 2
                                            message =
                                                "Wrong! 2 points added to the jackpot (Subtraction)."
                                        }

                                        correctAnswer == num1 * num2 -> {
                                            jackpot += 3
                                            message =
                                                "Wrong! 3 points added to the jackpot (Multiplication)."
                                        }

                                        else -> {
                                            jackpot += 1 // Default fallback for unexpected cases
                                            message = "Wrong! 1 point added to the jackpot."
                                        }
                                    }
                                }
                            }
                        }

                        playerAnswer = TextFieldValue("")
                        problemSolved = true

                        // Check if a player wins
                        if (player1Score >= 20 || player2Score >= 20) {
                            isWinner = true
                            winnerMessage =
                                if (player1Score >= 20) "Player 1 Wins!" else "Player 2 Wins!"
                        } else {
                            currentPlayer = if (currentPlayer == 1) 2 else 1
                        }
                    },
                    enabled = !problemSolved
                )
                {
                    Text("Guess")
                }

                // Play Again Button
                if (isWinner) {
                    Button(
                        onClick = {
                            // Reset the game
                            player1Score = 0
                            player2Score = 0
                            jackpot = 5
                            currentPlayer = 1
                            isWinner = false
                            winnerMessage = ""
                            message = ""
                            playerAnswer = TextFieldValue("")
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
    }
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