package com.example.snakegame

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class State(val food: Pair <Int, Int>, val snake: List<Pair<Int, Int>>, var score: Int, var time: Int)

class Game(private val scope: CoroutineScope, private var snakeSpeed: Float = 1f){

    private val mutex = Mutex()
    private val mutableState =
        MutableStateFlow(State(food = Pair(5, 5), snake = listOf(Pair(7, 7)), score = 0, time = 0))
    val state: Flow<State> = mutableState
    var move = 1 to 0
        set(value) {
            scope.launch {
                mutex.withLock { field = value }
            }
        }
    var callback: (() -> Unit)? = null
    private var isOver = false
    var moveDone = true
    val score
        get() = mutableState.value.score
    val time
        get() = mutableState.value.time
    var newRecord = false
    fun start() {
        scope.launch {
            delay(1000)
            while(isOver.not()) {
                mutableState.update {
                    it.apply {time++}
                }
                delay(1000)
            }
        }
        scope.launch {
            var snakeLength = 4
            Log.i("Game cycle", "Start!")
            MainLoop@
            while(isOver.not()) {
                delay((500 / snakeSpeed).toLong())
                mutableState.update {
                    // Snake move
                    val newPosition = mutableState.value.snake.first().let {poz ->
                        mutex.withLock {
                            Pair(
                                (poz.first + move.first + BOARD_SIZE) % BOARD_SIZE,
                                (poz.second + move.second + BOARD_SIZE) % BOARD_SIZE
                            )
                        }
                    }
                    // If snake eat itself
                    if(it.snake.contains(newPosition) && it.snake.last() != newPosition) {
                        isOver = true
                    }
                    // If snake eat food
                    if(newPosition == it.food) {
                        snakeLength++
                        it.score += 1 + ((snakeSpeed - 1) * 2).toInt()
                    }
                    // New (or not) food position
                    var foodNewPosition = if(newPosition == it.food) Random.nextInt(BOARD_SIZE) to Random.nextInt(BOARD_SIZE) else it.food
                    // Change food position while new food position equals position of snake body part.
                    while(it.snake.contains(foodNewPosition)) {
                        foodNewPosition = Random.nextInt(BOARD_SIZE) to Random.nextInt(BOARD_SIZE)
                    }
                    // Update
                    it.copy(
                        food = foodNewPosition,
                        snake = listOf(newPosition) + it.snake.take(snakeLength - 1)
                    )
                }
                if(!moveDone) moveDone = true
            }
            callback?.invoke()
        }
    }
    companion object {
        const val BOARD_SIZE = 16
    }
}