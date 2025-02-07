package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dicegame.ui.theme.DiceGameTheme
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

@Composable
fun DiceGameScreen() {
    // Game state variables
    var currentPlayer by remember { mutableStateOf(1) }
    var player1Score by remember { mutableStateOf(0) }
    var player2Score by remember { mutableStateOf(0) }
    var currentDiceRoll by remember { mutableStateOf(1) }
    var jackpot by remember { mutableStateOf(5) }
    var message by remember { mutableStateOf("") }

    // Math problem variables
    var num1 by remember { mutableStateOf(0) }
    var num2 by remember { mutableStateOf(0) }
    var correctAnswer by remember { mutableStateOf(0) }
    var playerAnswer by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Display current game state
        Text(text = "Current Player: P$currentPlayer")
        Text(text = "Player 1 Score: $player1Score | Player 2 Score: $player2Score")
        Text(text = "Jackpot: $jackpot")
        Text(text = "Dice Roll: $currentDiceRoll")

        // Button to roll the dice
        Button(onClick = {
            // Roll the dice
            currentDiceRoll = Random.nextInt(1, 7)

            when (currentDiceRoll) {
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
                    num1 = Random.nextInt(0, 100)
                    num2 = Random.nextInt(0, 100)
                    correctAnswer = num1 + num2
                    message = "Double Points! Solve: $num1 + $num2"
                }
                5 -> {
                    message = "Lose a turn! No problem to solve."
                    currentPlayer = if (currentPlayer == 1) 2 else 1
                }
                6 -> {
                    num1 = Random.nextInt(0, 100)
                    num2 = Random.nextInt(0, 100)
                    correctAnswer = num1 + num2
                    message = "Jackpot! Solve: $num1 + $num2 to win $jackpot points."
                }
            }
        }) {
            Text(text = "Roll Dice")
        }

        // Display feedback
        Text(text = message)

        // TextField for player's answer
        if (currentDiceRoll in 1..4 || currentDiceRoll == 6) {
            TextField(
                value = playerAnswer,
                onValueChange = { playerAnswer = it },
                label = { Text("Your Answer") }
            )
            Button(onClick = {
                // Validate the answer
                val answer = playerAnswer.text.toIntOrNull()
                if (answer == correctAnswer) {
                    message = "Correct!"
                    if (currentDiceRoll == 4) { // Double points
                        if (currentPlayer == 1) player1Score += currentDiceRoll * 2
                        else player2Score += currentDiceRoll * 2
                    } else if (currentDiceRoll == 6) { // Jackpot
                        if (currentPlayer == 1) player1Score += jackpot
                        else player2Score += jackpot
                        jackpot = 5 // Reset jackpot
                    } else { // Regular points
                        if (currentPlayer == 1) player1Score += currentDiceRoll
                        else player2Score += currentDiceRoll
                    }
                } else {
                    message = "Wrong! Points added to jackpot."
                    jackpot += currentDiceRoll
                }

                // Clear answer and switch turn
                playerAnswer = TextFieldValue("")
                if (player1Score >= 20 || player2Score >= 20) {
                    message = if (player1Score >= 20) "Player 1 Wins!" else "Player 2 Wins!"
                    player1Score = 0
                    player2Score = 0
                    jackpot = 5
                    currentPlayer = 1
                } else {
                    currentPlayer = if (currentPlayer == 1) 2 else 1
                }
            }) {
                Text("Submit Answer")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiceGamePreview() {
    DiceGameTheme {
        DiceGameScreen()
    }
}
