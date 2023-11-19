package dev.funkymuse.fosho.sample.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.funkymuse.fosho.sample.theme.FoShoLibThemeSurface

@Composable
internal fun HomeScreen(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onClick) {
            Text(text = "Login")
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    FoShoLibThemeSurface {
        HomeScreen {

        }
    }
}