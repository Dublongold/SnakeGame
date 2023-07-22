package com.example.snakegame

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.snakegame.ui.theme.DarkGreen
import com.example.snakegame.ui.theme.ui.theme.SnakeGameTheme

@Composable
fun GameStart(game: Game, navigateTo: ((String) -> Unit)?) {
    val context = LocalContext.current
    var gameOver by remember {
        mutableStateOf(false)
    }
    if(game.callback == null) {
        game.callback =  {
            gameOver = true
            val previousScore = getIntSavedData(context, SavedDataNames.SCORE)
            val score = game.score
            if(previousScore < score) {
                game.newRecord = true
                saveData(context, SavedDataNames.SCORE, score)
            }
        }
        game.start()
    }

    GameField(game, gameOver)
    GameOverField(gameOver, game, navigateTo)
}
@Composable
fun GameField(game: Game, gameOver: Boolean) {
    val state = game.state.collectAsState(initial = null)

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .draggable(
                    state = DraggableState {
                        if (game.moveDone) game.moveDone = false
                        else return@DraggableState
                        game.move =
                            if (it > 0 && game.move.second != -1)
                                0 to 1
                            else
                                if (game.move.second != 1)
                                    0 to -1
                                else
                                    game.move
                    },
                    orientation = Orientation.Vertical,
                    enabled = !gameOver
                )
                .draggable(
                    DraggableState {
                        if (game.moveDone) game.moveDone = false
                        else return@DraggableState
                        game.move =
                            if (it > 0 && game.move.first != -1)
                                1 to 0
                            else
                                if (game.move.first != 1)
                                    -1 to 0
                                else
                                    game.move
                    }, orientation = Orientation.Horizontal,
                    enabled = !gameOver
                )
                .fillMaxSize()
        ) {
            state.value?.let {
                Board(it, gameOver)
            }
        }
        // Score and time
        if(!gameOver) {
            state.value?.let {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(text = stringResource(R.string.score, it.score))
                    Text(text = stringResource(R.string.time, it.time))
                }
            }
        }
    }
}

@Composable
fun Board(state: State, gameOver: Boolean) {
    BoxWithConstraints(Modifier.padding(16.dp)) {
        val tileSize = maxHeight / Game.BOARD_SIZE
        // Game borders
        Box(
            Modifier
                .size(maxHeight)
                .padding(1.dp)
                .border(2.dp, Color.Gray)
        )
        // Food
        Box(
            Modifier
                .offset(
                    x = tileSize * state.food.first,
                    y = tileSize * state.food.second
                )
                .size(tileSize)
                .background(
                    Color.Red, CircleShape
                ))
        // Snake
        var firstSnakePart = true
        for(snake in state.snake) {
            if(firstSnakePart) {
                firstSnakePart = false
                continue
            }
            Box(
                modifier = Modifier
                    .offset(x = tileSize * snake.first, y = tileSize * snake.second)
                    .size(tileSize)
                    .background(
                        DarkGreen,
                        Shapes().small
                    )
            )
        }
        // Snake head
        state.snake.first().let { snake ->
        Box(
            modifier = Modifier
                .offset(x = tileSize * snake.first, y = tileSize * snake.second)
                .size(tileSize)
                .background(
                    Color.Green,
                    Shapes().small
                )
                .border(
                    1.dp,
                    if (gameOver)
                        Color.Red
                    else
                        Color.Green,
                    shape = Shapes().small
                )
        )
        }
    }
}

@Composable
fun GameOverField(gameOver: Boolean, game: Game, navigateTo: ((String) -> Unit)?) {
    // Game over field body
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if(gameOver) {
            // Background
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x77333333)))
        }
        // Game over field content
        AnimatedVisibility(visible = gameOver,
            enter = slideInVertically(),
            exit = slideOutVertically(),
        ) {
            GameOverFieldContent(game, navigateTo)
        }
    }
}
@Composable
fun GameOverFieldContent(game: Game, navigateTo: ((String) -> Unit)?) {
    // Game over field content body
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        // Border and content
        Box(
            Modifier
                .fillMaxWidth(0.5f)
                .wrapContentHeight()
                .border(2.dp, Color.Red)
                .background(Color.Black),
            contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Game over text
                Text(text = stringResource(id = R.string.game_over),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
                // User score
                Text(
                    // Before change from hardcode string to string resource:
                    // if(game.newRecord) "\nNew record!" else "")"Score: ${game.score}${if(game.newRecord) "\nNew record!" else ""}\nTime: ${game.time}"
                    text = stringResource(
                        R.string.game_result,
                            game.score,
                            if(game.newRecord) stringResource(R.string.new_record_part) else "",
                            game.time),
                    textAlign = TextAlign.Center
                )
                // Buttons
                Row(modifier = Modifier.padding(vertical = 5.dp)) {
                    // Retry button
                    Button(
                        onClick = {navigateTo?.invoke("retry")},
                        modifier = Modifier.padding(end = 5.dp)) {
                        Text(
                            text = stringResource(id = R.string.play_gain),
                            color = Color.Black
                        )
                    }
                    // Go to menu button
                    Button({ navigateTo?.invoke("menu") }) {
                        Text(
                            text = stringResource(id = R.string.go_to_menu),
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = PreviewGlobalConfiguration.widthDp,
    heightDp = PreviewGlobalConfiguration.heightDp)
@Composable
fun GameActivityPreview() {
    SnakeGameTheme {
        GameStart(Game(LocalLifecycleOwner.current.lifecycleScope, 1f), null)
    }
}