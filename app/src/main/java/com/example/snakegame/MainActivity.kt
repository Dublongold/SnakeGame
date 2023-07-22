package com.example.snakegame

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.snakegame.ui.theme.SnakeGameTheme

class MainActivity : ComponentActivity() {
    var destination = "menu"
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var changeDestination: (String) -> Unit
    override fun onCreate(savedInstanceState: Bundle?) {
        val mediaPlayer = MediaPlayer.create(this, R.raw.background_sound)
        mediaPlayer.isLooping = true
        val tempMusicVolume = getFloatSavedData(this@MainActivity, SavedDataNames.MUSIC_VOLUME)
        mediaPlayer.setVolume(tempMusicVolume, tempMusicVolume)
        super.onCreate(savedInstanceState)
        mediaPlayer.start()
        setContent {
            var currentLanguage by remember {
                mutableStateOf(getLanguageSavedData(this))
            }
            var musicVolume by remember {
                mutableStateOf(getFloatSavedData(this@MainActivity, SavedDataNames.MUSIC_VOLUME))
            }
            changeLanguage(this, currentLanguage)
            mediaPlayer.setVolume(musicVolume, musicVolume)
            var destination by remember {
                mutableStateOf(destination)
            }

            this.destination = destination
            val navigateTo: (String) -> Unit = { dest: String -> destination = dest }
            changeDestination = navigateTo

            BackHandler{
                if(destination != "menu") {
                    changeDestination("menu")
                }
                else {
                    this.finish()
                }
            }
            SnakeGameTheme {
                when(destination) {
                    "menu" -> {
                        Menu(navigateTo)
                    }
                    "options" -> {
                        Options(
                            navigateTo = navigateTo,
                            currentLanguage = currentLanguage,
                            musicVolume = musicVolume,
                            changeLanguageCallback = {currentLanguage = it},
                            changeMusicVolume = {musicVolume = it})
                    }
                    "game" -> {
                        val snakeSpeed = getFloatSavedData(this@MainActivity, SavedDataNames.SPEED)
                        val game = Game(lifecycleScope, snakeSpeed)
                        GameStart(game, navigateTo)
                    }
                    "retry" -> {
                        destination = "game"
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        super.onDestroy()
    }
}