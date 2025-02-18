package cat.itb.m78.exercices

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import m78exercices.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource

class QuestionViewModel : ViewModel() {
    private val _gameState = mutableStateOf(GameState())
    val gameState: State<GameState> get() = _gameState
    private val questions = getQuestions()
    var rounds: Int = 10

    fun onAnswerSelected(isCorrect: Boolean) {
        val newScore = if (isCorrect) _gameState.value.score + 1 else _gameState.value.score
        val newRound = _gameState.value.round + 1
        _gameState.value = _gameState.value.copy(score = newScore, round = newRound)
    }

    fun resetGame() {
        _gameState.value = GameState()
    }

    fun getCurrentQuestion(): Question? {
        return if (_gameState.value.round <= rounds && _gameState.value.round <= questions.size) {
            questions[_gameState.value.round - 1]
        } else null
    }

    data class GameState(val score: Int = 0, val round: Int = 1)
}

data class Question(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

fun getQuestions(): List<Question> = listOf(
    Question("Question1", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 0),
    Question("Question2", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 1),
    Question("Question3", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 2),
    Question("Question4", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 1),
    Question("Question5", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 0),
    Question("Question6", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 1),
    Question("Question7", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 2),
    Question("Question8", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 3),
    Question("Question9", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 1),
    Question("Question10", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 2),
    Question("Question11", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 1),
    Question("Question12", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 3),
    Question("Question13", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 2),
    Question("Question14", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 1),
    Question("Question15", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 0),
    Question("Question16", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 3),
    Question("Question17", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 1),
    Question("Question18", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 2),
    Question("Question19", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 1),
    Question("Question20", listOf("Answer1", "Answer2", "Answer3", "Answer4"), 3),


)

@Composable
fun MenuScreen(navigateToSettingsScreen: () -> Unit, navigateToGameScreen: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
/*
        Image(
            painter = painterResource(Res.drawable.TriviaLogo),
            modifier = Modifier.size(150.dp),
            contentDescription = null
        )
*/
        Button(onClick = navigateToGameScreen) { Text("New Game") }
        Button(onClick = navigateToSettingsScreen) { Text("Settings") }
    }
}

@Composable
fun GameScreen(viewModel: QuestionViewModel, navigateToResultScreen: () -> Unit) {
    val state by viewModel.gameState
    val question = viewModel.getCurrentQuestion()
    if (question == null) {
        navigateToResultScreen()
        return
    }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Round ${state.round} of ${viewModel.rounds}")
        Text(text = question.question)
        question.options.forEachIndexed { index, option ->
            Button(
                onClick = {
                    selectedAnswer = index
                    viewModel.onAnswerSelected(index == question.correctAnswerIndex)
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text(option)
            }
        }
    }
}

@Composable
fun ResultScreen(viewModel: QuestionViewModel, navigateToMenuScreen: () -> Unit) {
    val state by viewModel.gameState
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Score: ${state.score}")
        Button(onClick = {
            viewModel.resetGame()
            navigateToMenuScreen()
        }) { Text("Go to Menu") }
    }
}

@Composable
fun SettingsScreen(viewModel: QuestionViewModel, navigateToMenuScreen: () -> Unit) {
    var selectedRounds by remember { mutableStateOf(viewModel.rounds) }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select the number of rounds: ${selectedRounds}")
        Slider(
            value = selectedRounds.toFloat(),
            onValueChange = { selectedRounds = it.toInt() },
            valueRange = 1f..20f,
            steps = 19,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Button(onClick = {
            viewModel.rounds = selectedRounds
            navigateToMenuScreen()
        }) { Text("Save and Back to Menu") }
    }
}

object Destination {
    @Serializable object MenuScreen
    @Serializable object GameScreen
    @Serializable object ResultScreen
    @Serializable object SettingsScreen
}

@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel: QuestionViewModel = viewModel()
    NavHost(navController = navController, startDestination = Destination.MenuScreen) {
        composable<Destination.MenuScreen> {
            MenuScreen(
                navigateToGameScreen = { navController.navigate(Destination.GameScreen) },
                navigateToSettingsScreen = { navController.navigate(Destination.SettingsScreen) }
            )
        }
        composable<Destination.GameScreen> {
            GameScreen(
                viewModel = viewModel,
                navigateToResultScreen = { navController.navigate(Destination.ResultScreen) }
            )
        }
        composable<Destination.SettingsScreen> {
            SettingsScreen(
                viewModel = viewModel,
                navigateToMenuScreen = { navController.navigate(Destination.MenuScreen) }
            )
        }
        composable<Destination.ResultScreen> {
            ResultScreen(
                viewModel = viewModel,
                navigateToMenuScreen = { navController.navigate(Destination.MenuScreen) }
            )
        }
    }
}