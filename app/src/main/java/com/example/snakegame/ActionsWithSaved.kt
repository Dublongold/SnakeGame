package com.example.snakegame

import android.content.Context
import androidx.compose.ui.text.intl.Locale

object SavedDataNames {
    const val SPEED = "speed"
    const val MUSIC_VOLUME = "musicVolume"
    const val LANGUAGE = "language"
    const val SCORE = "maxScore"
}

fun getFloatSavedData(context: Context, name: String): Float {
    val sharedPreferences = context.getSharedPreferences("userSetup", Context.MODE_PRIVATE)
    return if(sharedPreferences.contains(name)) {
        sharedPreferences.getFloat(name, 0f)
    }
    else {
        when(name) {
            SavedDataNames.SPEED -> 1f
            SavedDataNames.MUSIC_VOLUME -> 1f
            else -> 0f
        }
    }
}

fun getIntSavedData(context: Context, name: String): Int {
    val sharedPreferences = context.getSharedPreferences("userSetup", Context.MODE_PRIVATE)
    return if(sharedPreferences.contains(name)) {
        sharedPreferences.getInt(name, 0)
    }
    else {
        when(name) {
            SavedDataNames.SCORE -> -1
            else -> 0
        }
    }
}

fun getLanguageSavedData(context: Context): Language {
    val sharedPreferences = context.getSharedPreferences("userSetup", Context.MODE_PRIVATE)
    var currentLanguage = sharedPreferences.getString(SavedDataNames.LANGUAGE, "")
    if(currentLanguage.isNullOrEmpty()) {
        val curLoc = Locale.current.toString()
        currentLanguage = when {
            curLoc.startsWith("uk-") -> "ukr"
            curLoc.startsWith("ru-") -> "rus"
            else -> "eng"
        }
    }
    return Language[currentLanguage]
}

fun<T> saveData(context: Context, name: String, value: T) {
    val sharedPreferences = context.getSharedPreferences("userSetup", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    when(name) {
        SavedDataNames.SPEED -> {
            if(value is Float)
                editor.putFloat(name, value)
            else
                throw IllegalArgumentException("The value type is incompatible with the name $name.")
        }
        SavedDataNames.MUSIC_VOLUME ->
            if(value is Float)
                editor.putFloat(name, value)
            else
                throw IllegalArgumentException("The value type is incompatible with the name $name.")
        SavedDataNames.LANGUAGE ->
            if(value is Language)
                editor.putString(name, value.toString())
            else
                throw IllegalArgumentException("The value type is incompatible with the name $name.")
        SavedDataNames.SCORE ->
            if(value is Int)
                editor.putInt(name, value)
            else
                throw IllegalArgumentException("The value type is incompatible with the name $name.")
        else -> throw IllegalArgumentException("Invalid data name for save: $name")
    }
    editor.apply()
}