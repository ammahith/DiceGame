package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.dicegame.ui.theme.DiceGameTheme
import androidx.compose.ui.tooling.preview.Preview

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
        // Player scores and jackpot
        Text(text = "Player 1 total: $player1Score")
        Text(text = "Player 2 total: $player2Score")
        Text(text = "Current Jackpot: $jackpot")

        // Current player
        Text(text = "Current Player: P$currentPlayer")

        // Dice image
        Image(
            painter = painterResource(id = getDiceImage(currentDiceRoll)),
            contentDescription = "Dice Roll",
            modifier = Modifier.size(100.dp)
        )

        // Button to roll the dice
        Button(onClick = {
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
            Text(text = "Roll Die")
        }

        // Math problem and answer input
        Text(text = message)
        if (currentDiceRoll in 1..4 || currentDiceRoll == 6) {
            TextField(
                value = playerAnswer,
                onValueChange = { playerAnswer = it },
                label = { Text("Your Answer") }
            )
            Button(onClick = {
                val answer = playerAnswer.text.toIntOrNull()
                if (answer == correctAnswer) {
                    message = "Correct!"
                    if (currentPlayer == 1) player1Score += currentDiceRoll else player2Score += currentDiceRoll
                } else {
                    message = "Wrong! Jackpot increases."
                    jackpot += currentDiceRoll
                }
                playerAnswer = TextFieldValue("")
                currentPlayer = if (currentPlayer == 1) 2 else 1
            }) {
                Text("Guess")
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
