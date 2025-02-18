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
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import m78exercices.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource


// ViewModel
class QuestionViewModel : ViewModel() {
    // Variable per guardar la puntuació i la ronda actual
    private val _gameState = mutableStateOf(GameState())
    val gameState: State<GameState> get() = _gameState
    // Llista de preguntes
    private val questions = getQuestions()
    // Rondes per defecte
    var rounds: Int = 10


    // Funció que es crida quan l'usuari escull una resposta
    fun onAnswerSelected(isCorrect: Boolean) {
        val newScore = if (isCorrect) _gameState.value.score + 1 else _gameState.value.score
        val newRound = _gameState.value.round + 1
        _gameState.value = _gameState.value.copy(score = newScore, round = newRound)
    }


    // Funció per reiniciar el joc
    fun resetGame() {
        _gameState.value = GameState()
    }


    // Funció per obtenir la pregunta actual segons la ronda
    fun getCurrentQuestion(): Question? {
        return if (_gameState.value.round <= rounds && _gameState.value.round <= questions.size) {
            questions[_gameState.value.round - 1]
        } else null
    }
    // Estat del joc
    data class GameState(val score: Int = 0, val round: Int = 1)
}


// Classe Question
data class Question(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)


// Fució per obtenir la llista de preguntes
fun getQuestions(): List<Question> = listOf(
    Question("1. What is the most durable material in the game?", listOf("a) Iron", "b) Diamond", "c) Netherite", "d) Gold"), 2),
    Question("2. How tall is a Minecraft player?", listOf("a) 1.5 blocks", "b) 2 blocks", "c) 1.8 blocks", "d) 2.5 blocks"), 1),
    Question("3. Which mob is friendly to the player and can help fight?", listOf("a) Skeleton", "b) Wolf", "c) Enderman", "d) Ravager"), 1),
    Question("4. Which tool is best for breaking stone?", listOf("a) Sword", "b) Shovel", "c) Axe", "d) Pickaxe"), 3),
    Question("5. Which mob attacks using arrows?", listOf("a) Creeper", "b) Skeleton", "c) Enderman", "d) Iron Golem"),1),
    Question("6. Which food restores the most hunger?", listOf("a) Carrot", "b) Cooked Steak", "c) Mushroom Stew", "d) Raw Cod"), 1),
    Question("7. What is the main ingredient for making glass?", listOf("a) Water", "b) Sand", "c) Stone", "d) Milk"), 1),
    Question("8. Which structure naturally generates in the Nether?", listOf("a) Abandoned Mineshafts", "b) End Cities", "c) Nether Fortresses", "d) Woodland Mansions"), 2),
    Question("9. Which mob explodes when it gets close to the player?", listOf("a) Zombie", "b) Phantom", "c) Creeper", "d) Wither"), 3),
    Question("10. Which mineral can only be found in the Nether?", listOf("a) Copper", "b) Redstone", "c) Netherite", "d) Lapis Lazuli"), 2),
    Question("11. Which mob drops Ender Pearls when killed?", listOf("a) Enderman", "b) Blaze", "c) Skeleton", "d) Ghast"), 0),
    Question("12. What do you need to activate an End Portal?", listOf("a) Dragon Eggs", "b) Eyes of Ender", "c) Nether Stars", "d) Diamond Blocks"), 1),
    Question("13. Which of these mobs does not burn in sunlight?", listOf("a) Skeleton", "b) Zombie", "c) Spider", "d) Zombie Pigman"), 2),
    Question("14. How many diamonds do you need to craft a diamond sword?", listOf("a) 1", "b) 2", "c) 3", "d) 4"), 1),
    Question("15. What is the maximum number of Eyes of Ender needed to activate an End Portal?", listOf("a) 10", "b) 12", "c) 14", "d) 16"), 1),
    Question("16. Which potion allows you to fly in survival mode?", listOf("a) Potion of Levitation", "b) Potion of Speed", "c) There is no potion for flying", "d) Potion of Invisibility"), 2),
    Question("17. Which mob is found deep underground and detects vibrations?", listOf("a) Warden", "b) Wither", "c) Snow Golem", "d) Piglin"), 0),
    Question("18. How many blocks are needed to craft an Enchanting Table?", listOf("a) 3 Obsidian and 2 Diamonds", "b) 4 Obsidian and 2 Diamonds", "c) 2 Obsidian and 3 Diamonds", "d) 4 Obsidian and 1 Diamond"), 1),
    Question("19. Which item allows you to breathe underwater?", listOf("a) Iron Helmet", "b) Turtle Helmet", "c) Rope", "d) Smooth Stone"), 1),
    Question("20. What is the name of Minecraft’s original creator?", listOf("a) Markus Persson", "b) Jens Bergensten", "c) Bill Gates", "d) Steve Notch"), 0),
)


// Pantalla Menú principal
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
        // Botó iniciar partida
        Button(onClick = navigateToGameScreen) { Text("New Game") }
        // Botó opcions
        Button(onClick = navigateToSettingsScreen) { Text("Settings") }
    }
}


// Pantalla GameScreen
@Composable
fun GameScreen(viewModel: QuestionViewModel, navigateToResultScreen: () -> Unit) {
    // Estat del joc
    val state by viewModel.gameState
    val question = viewModel.getCurrentQuestion()
    // Si no hi ha més preguntes, navegar a la pantalla de resultats
    if (question == null) {
        navigateToResultScreen()
        return
    }
    // Variable per gestionar la resposta que es selecciona
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    // Variable per emmagatzemar el temps restant (10 segons)
    var timeLeft by remember { mutableStateOf(10) }


    // Cada cop que la pregunta canvia es fa un reset al temps
    LaunchedEffect(key1 = question) {
        timeLeft = 10
        selectedAnswer = null


        // Bucle per contar els segons
        while (timeLeft > 0 && selectedAnswer == null) {
            delay(1000L) // 1 segon
            timeLeft--
        }


        // Si s'acaba el temps compta com a resposta errònia
        if (selectedAnswer == null) {
            viewModel.onAnswerSelected(false)
        }
    }


    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Round ${state.round} of ${viewModel.rounds}")


        Spacer(Modifier.height(20.dp))


        Text("Time left: $timeLeft seconds")


        Spacer(Modifier.height(40.dp))


        Text(text = question.question)
        // Botons per seleccionar resposta
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


// Pantalla Resultat
@Composable
fun ResultScreen(viewModel: QuestionViewModel, navigateToMenuScreen: () -> Unit) {
    val state by viewModel.gameState
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Score: ${state.score}")
        // Botó per tornar al menú i reiniciar el joc
        Button(onClick = {
            viewModel.resetGame()
            navigateToMenuScreen()
        }) { Text("Go to Menu") }
    }
}


// Pantalla Settings
@Composable
fun SettingsScreen(viewModel: QuestionViewModel, navigateToMenuScreen: () -> Unit) {
    // Variable per emmagatzemar el nombre de rondes seleccionades
    var selectedRounds by remember { mutableStateOf(viewModel.rounds) }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select the number of rounds: ${selectedRounds}")
        // Slider per seleccionar el número de rondes
        Slider(
            value = selectedRounds.toFloat(),
            onValueChange = { selectedRounds = it.toInt() },
            valueRange = 1f..20f,
            steps = 19,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        // Botó per guardar la configuració i tornar al menú
        Button(onClick = {
            viewModel.rounds = selectedRounds
            navigateToMenuScreen()
        }) { Text("Save and Back to Menu") }
    }
}


// Objecte per guardar les destinacions
object Destination {
    @Serializable object MenuScreen
    @Serializable object GameScreen
    @Serializable object ResultScreen
    @Serializable object SettingsScreen
}


// Main
@Composable
fun App() {
    // Controlador de navegació
    val navController = rememberNavController()
    val viewModel: QuestionViewModel = viewModel()
    // Rutes de navegació
    NavHost(navController = navController, startDestination = Destination.MenuScreen) {
        // Menu Screen
        composable<Destination.MenuScreen> {
            MenuScreen(
                navigateToGameScreen = { navController.navigate(Destination.GameScreen) },
                navigateToSettingsScreen = { navController.navigate(Destination.SettingsScreen) }
            )
        }
        // Game Screen
        composable<Destination.GameScreen> {
            GameScreen(
                viewModel = viewModel,
                navigateToResultScreen = { navController.navigate(Destination.ResultScreen) }
            )
        }
        // Settings Screen
        composable<Destination.SettingsScreen> {
            SettingsScreen(
                viewModel = viewModel,
                navigateToMenuScreen = { navController.navigate(Destination.MenuScreen) }
            )
        }
        // Result Screen
        composable<Destination.ResultScreen> {
            ResultScreen(
                viewModel = viewModel,
                navigateToMenuScreen = { navController.navigate(Destination.MenuScreen) }
            )
        }
    }
}
