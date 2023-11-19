package dev.funkymuse.fosho.sample.features.dialogs.yellow

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.funkymuse.fosho.navigator.android.OnSingleStringCallbackArgument
import dev.funkymuse.fosho.navigator.android.RedDialogCallbackArguments
import dev.funkymuse.fosho.navigator.android.RedDialogDestination
import dev.funkymuse.fosho.navigator.android.YellowDialogDestination
import dev.funkymuse.fosho.navigator.android.navigator.impl.Navigator
import dev.funkymuse.fosho.navigator.codegen.annotation.Content

@Content
internal object YellowContent : YellowDialogDestination {
    @Composable
    override fun Content() {
        var callbackString by remember { mutableStateOf<String?>(null) }
        RedDialogCallbackArguments.rememberRedDialogCallbackArguments()
            .OnSingleStringCallbackArgument(
                key = RedDialogCallbackArguments.PICKED_TEXT,
                onValue = {
                    if (it != null) {
                        callbackString = it
                    }
                })
        Column(
            modifier = Modifier
                .fillMaxHeight(0.6f)
                .fillMaxWidth(0.6f)
                .background(Color.Yellow.copy(alpha = 0.9f), RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                Navigator.navigateSingleTop(
                    RedDialogDestination.openRedDialog(
                        arrayOf(
                            "Navigation",
                            "Compose",
                            "Something",
                            "Dialog",
                        )
                    )
                )
            }) {
                Text(text = "Open other dialog")
            }
            AnimatedVisibility(
                visible = !callbackString.isNullOrEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    color = Color.Black,
                    text = "Callback string $callbackString",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}