package com.example.snakegame

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Language: Parcelable {
    ENGLISH,
    UKRAINIAN,
    RUSSIAN;
    companion object {
        operator fun get(index: String): Language {
            return when(index.lowercase().take(3)) {
                "eng" -> ENGLISH
                "ukr" -> UKRAINIAN
                "rus" -> RUSSIAN
                else -> throw IllegalArgumentException("Illegal language: $index")
            }
        }
    }
}
//data class StringResource(val english: String, val russian: String, val ukrainian: String)
//
//object LocaleStringController {
//    val appName = "SnakeGame"
//    val appTitle = StringResource("Snake", )
//    val startButton = "Start"
//    val optionsButton = "Options"
//    val exitButton = "Exit"
//    val languageText = "Language:"
//    val speedText = "Speed: %s"
//    val musicVolume = "Music volume: %d%%"
//    val scoreInfo = "Score: %s"
//    val maxScoreInfo = "Max score: %s"
//    val credits = "Created by Dublongold in Android Studio with Jetpack Compose."
//    val english = "English"
//    val ukrainian = "Ukrainian"
//    val russian = "Russian"
//    val time = "Time: %ss"
//    val gameOver = "Game over"
//    val gameResult = """Score: %s%s"
//    Time: %s"""
//    val newRecordPart = """
//    New record!"""
//    val playGain = "Play again"
//    val goToMenu = "Go to menu"
//}
//
//<string name="app_name">SnakeGame</string>
//<string name="app_title">Змейка</string>
//<string name="start_button">Начать</string>
//<string name="options_button">Настройки</string>
//<string name="exit_button">Выход</string>
//<string name="language_text">"Язык: "</string>
//<string name="speed_text">Скорость: %s</string>
//<string name="music_volume">Громкость музыки: %d%%</string>
//<string name="score_info">Счёт: %s</string>
//<string name="max_score_info">Максимальний счёт: %s</string>
//<string name="credits">Создано Dublongold\'ом в Android Studio с помощью Jetpack Compose</string>
//<string name="english">Английский</string>
//<string name="ukrainian">Украинский</string>
//<string name="russian">Русский</string>
//<string name="time">Время: %sс</string>
//<string name="game_over">Игра окончена</string>
//<string name="game_result">Score: %s%s
//Time: %s</string>
//<string name="new_record_part">
//Новый рекорд!</string>
//<string name="play_gain">Сыграть ещё</string>
//<string name="go_to_menu">Вернуться в меню</string>