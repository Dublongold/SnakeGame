package com.example.snakegame

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snakegame.ui.theme.SnakeGameTheme

@Composable
fun Menu(navigateTo: ((String) -> Unit)?) {

    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        ExitIconButton(context,
            Modifier.align(Alignment.TopEnd))
        OptionsIconButton(navigateTo,
            Modifier.align(Alignment.TopStart))
        Column(Modifier.fillMaxSize()) {
            TitleText(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Speed()
                StartButton(navigateTo)
            }
            Credits(
                Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun TitleText(modifier: Modifier = Modifier) {
    val maxScore = getIntSavedData(LocalContext.current, SavedDataNames.SCORE)
    Box(modifier = modifier,
        contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(id = R.string.app_title),
            fontSize = 50.sp,
            textAlign = TextAlign.Right)
        if(maxScore > 0) {
            Text(
                text = stringResource(R.string.max_score_info, maxScore),
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        else {
            Log.i("Title text", "Max score: $maxScore")
        }
    }
}

@Composable
fun Speed() {
    val context = LocalContext.current
    var speedValue by remember { mutableStateOf(getFloatSavedData(context, SavedDataNames.SPEED)) }
    Text(
        text = stringResource(id = R.string.speed_text, speedValue),
        modifier = Modifier.offset(y = 10.dp)
    )
    Slider(
        value = speedValue,
        onValueChange = {
            speedValue = it
        },
        onValueChangeFinished = {
            saveData(context, SavedDataNames.SPEED, speedValue)
        },
        valueRange = 1f..3f,
        steps = 3,
        modifier = Modifier.fillMaxWidth(0.6f)
    )
}

@Composable
fun StartButton(navigateTo: ((String) -> Unit)?) {
    Button(
        content = { Text(
            stringResource(id = R.string.start_button),
            color = Color.Black) },
        onClick = {
            Log.i("Start button", "Click!")
            navigateTo?.invoke("game")
        }
    )
}
@Composable
fun OptionsIconButton(navigateTo: ((String) -> Unit)?, modifier: Modifier) {
    IconButton(
        onClick = {
            navigateTo?.invoke("options")
        },
        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White),
        modifier = modifier
    ){
        Icon(imageVector = Icons.Default.Settings,
            contentDescription = "Options")
    }
}
@Composable
fun ExitIconButton(context: Context, modifier: Modifier = Modifier) {
    IconButton(
        onClick = {
            Log.i("Exit button", "Click!")
            (context as? Activity)?.finish()
        },
        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White),
        modifier = modifier,
    ) {
        Icon(imageVector = Icons.Default.Close,
            contentDescription = "Exit")
    }
}
@Composable
fun Credits(modifier: Modifier = Modifier) {
    Box(modifier = modifier,
        contentAlignment = Alignment.BottomCenter){
        Text(
            text = stringResource(id = R.string.credits),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 15.dp),
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = PreviewGlobalConfiguration.widthDp,
    heightDp = PreviewGlobalConfiguration.heightDp)
@Composable
fun MenuActivityPreview() {
    SnakeGameTheme {
        Menu(null)
    }
}