package dev.funkymuse.fosho.sample.features.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import dev.funkymuse.fosho.sample.theme.FoShoLibThemeSurface


@Composable
internal fun LoginScreen(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray.copy(alpha = 0.65f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onClick) {
            Text(text = "Go back")
        }
    }
}

@Composable
@Preview
private fun LoginScreenPreview(){
    FoShoLibThemeSurface {
        LoginScreen {

        }
    }
}