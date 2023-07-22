package com.example.snakegame

import android.content.Context
import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.snakegame.ui.theme.MusicSliderColors
import com.example.snakegame.ui.theme.SnakeGameTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun Options(navigateTo: ((String) -> Unit)?, currentLanguage: Language, musicVolume: Float, changeLanguageCallback: (Language) -> Unit, changeMusicVolume: (Float) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val resources by remember {
        mutableStateOf(context.resources)
    }
    var wantClearData by remember {
        mutableStateOf(false)
    }
    var cancellationToken by remember {
        mutableStateOf(CancellationToken())
    }
    var userSavedDataCleared by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        GoBackButton(resources, navigateTo)
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 30.dp)
        ) {
            MusicVolumeComponents(resources, musicVolume, changeMusicVolume)
            Language(
                resources,
                currentLanguage,
                {
                    val newLang = Language.ENGLISH
                    changeLanguage(context, newLang)
                    changeLanguageCallback(newLang)
                },
                {
                    val newLang = Language.UKRAINIAN
                    changeLanguage(context, newLang)
                    changeLanguageCallback(newLang)
                },
                {
                    val newLang = Language.RUSSIAN
                    changeLanguage(context, newLang)
                    changeLanguageCallback(newLang)
                }
            )
            if(!wantClearData) {
                ClearSavedData(resources = resources,
                    cancellationToken = cancellationToken,
                    waitBeforeReturnOldState = {
                        lifecycleOwner.lifecycleScope.launch {
                            wantClearData = true
                            delay(3000)
                            if(!it.isCancelled) {
                                wantClearData = false
                            }
                        }
                    })
            }
            else {
                ConfirmClearSavedData(
                    resources = resources,
                    cancellationToken = cancellationToken,
                    confirm =  {
                        cancellationToken.cancel()
                        cancellationToken = CancellationToken()
                        wantClearData = false
                        lifecycleOwner.lifecycleScope.launch {
                            userSavedDataCleared = true
                            delay(1500)
                            userSavedDataCleared = false
                        }
                        changeLanguageCallback(getLanguageSavedData(context))
                    }
                )
            }
        }
        SavedDataClearedNotification(resources, userSavedDataCleared)
    }
}
@Composable
fun GoBackButton(resources: Resources, navigateTo: ((String) -> Unit)?) {
    Button(
        content = { Text(resources.getString(R.string.go_to_menu), color = Color.Black) },
        onClick = { navigateTo?.invoke("menu") })
}
@Suppress("DEPRECATION")
fun changeLanguage(context: Context, language: Language) {
    saveData(context, SavedDataNames.LANGUAGE, language)
    val res = context.resources
    val conf = res.configuration
    val lang = Locale(language.toString().take(2).lowercase())
    conf.setLocale(lang)
    context.createConfigurationContext(conf)
    res.updateConfiguration(conf, res.displayMetrics)
}
@Composable
fun MusicVolumeComponents(resources: Resources, musicVolume: Float, changeMusicVolume: (Float) -> Unit) {
    val context = LocalContext.current

    var musicVolumeTextField by remember { mutableStateOf((musicVolume * 100).toInt().toString())}

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = resources.getString(
                R.string.music_volume,
                (musicVolume * 100).toInt()
            ),
            modifier = Modifier.padding(end = 2.dp)
        )
        Slider(
            value = musicVolume,
            onValueChange = {
                changeMusicVolume(it)
                musicVolumeTextField = (it * 100).toInt().toString()
            },
            onValueChangeFinished = {
                saveData(context, SavedDataNames.MUSIC_VOLUME, musicVolume)
            },
            colors = MusicSliderColors,
            steps = 98)
    }
}

@Composable
fun Language(resources: Resources,
             currentLanguage: Language = Language.ENGLISH,
             changeLanguageEng: () -> Unit,
             changeLanguageUkr: () -> Unit,
             changeLanguageRus: () -> Unit) {
    val modifiers = Array<Modifier>(3) {Modifier}
    val i = when(currentLanguage) {
        Language.ENGLISH -> 0
        Language.UKRAINIAN -> 1
        Language.RUSSIAN -> 2
    }
    modifiers[i] = modifiers[i].background(color = Color.Gray, shape = RoundedCornerShape(15.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = Color.DarkGray,
                shape = RoundedCornerShape(15.dp)
            )
            .height(IntrinsicSize.Min)
    ) {
        LanguageText(resources)
        TextButton(onClick = changeLanguageEng, modifier = modifiers[0]) {
            Text(resources.getString(R.string.english))
        }
        TextButton(onClick = changeLanguageUkr, modifier = modifiers[1]) {
            Text(resources.getString(R.string.ukrainian))
        }
        TextButton(onClick = changeLanguageRus, modifier = modifiers[2]) {
            Text(resources.getString(R.string.russian))
        }
    }
}

@Composable
fun LanguageText(resources: Resources) {
    Text(
        text = resources.getString(R.string.language_text),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(start = 10.dp, end = 7.dp)
    )
}
@Composable
fun ClearSavedData(resources: Resources, waitBeforeReturnOldState: (CancellationToken) -> Unit, cancellationToken: CancellationToken) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = resources.getString(R.string.want_clear_data),
            modifier = Modifier.padding(end = 5.dp)
        )
        Button(
            onClick = {
                waitBeforeReturnOldState(cancellationToken)
            },
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
        ) {
            Text(
                text = resources.getString(R.string.clear),
                color = Color.Black
            )
        }
    }
}
@Composable
fun ConfirmClearSavedData(resources: Resources, confirm: (CancellationToken) -> Unit, cancellationToken: CancellationToken) {
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = resources.getString(R.string.confirm_clear_data),
            modifier = Modifier.padding(end = 5.dp)
        )
        Button(
            onClick = {
                val sharedPreferences = context.getSharedPreferences("userSetup", Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                confirm(cancellationToken)
            },
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
        ) {
            Text(
                text = resources.getString(R.string.confirm_clear),
                color = Color.Black
            )
        }
    }
}
@Composable
fun SavedDataClearedNotification(resources: Resources, visibility: Boolean) {
    AnimatedVisibility(visible = visibility,
    enter = slideInVertically(),
    exit = slideOutVertically()) {
        Box(Modifier.fillMaxSize()){
            Box(modifier = Modifier
                .border(1.dp, Color.Red)
                .background(Color.Black)
                .align(Alignment.TopCenter)) {
                Text (text = resources.getString(R.string.saved_data_cleared_successfully),
                modifier = Modifier.padding(7.dp))
            }
        }
    }
}
@Preview(
    showBackground = true,
    widthDp = PreviewGlobalConfiguration.widthDp,
    heightDp = PreviewGlobalConfiguration.heightDp
)
@Composable
fun OptionsPreview() {
    SnakeGameTheme {
        var currentLanguage by remember {
            mutableStateOf(Language.ENGLISH)
        }
        var musicVolume by remember {
            mutableStateOf(1f)
        }
        Options(navigateTo = null,
            currentLanguage = currentLanguage,
            musicVolume = musicVolume,
            changeLanguageCallback =  {currentLanguage = it},
            changeMusicVolume = {musicVolume = it})
    }
}