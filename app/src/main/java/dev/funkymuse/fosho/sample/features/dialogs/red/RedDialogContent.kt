package dev.funkymuse.fosho.sample.features.dialogs.red

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.funkymuse.fosho.navigator.android.RedDialogCallbackArguments
import dev.funkymuse.fosho.navigator.android.RedDialogDestination
import dev.funkymuse.fosho.navigator.android.navigator.impl.Navigator
import dev.funkymuse.fosho.navigator.codegen.annotation.Content

@Content
internal object RedDialogContent : RedDialogDestination {

    @Composable
    override fun Content() {
        val redDialogViewModel = hiltViewModel<RedDialogViewModel>()
        val texts by redDialogViewModel.texts.collectAsStateWithLifecycle()
        val argumentsFromRedDialog = RedDialogCallbackArguments.rememberRedDialogCallbackArguments()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Red.copy(alpha = 0.8f), RoundedCornerShape(24.dp)),
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                content = {
                    item {
                        Button(
                            onClick = {
                                argumentsFromRedDialog.addCallbackResult("Text argument sent back to Yellow")
                                Navigator.navigateUp()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Send arguments back", color = Color.White,
                            )
                        }
                    }
                    items(texts.orEmpty(), key = { it }) {
                        Text(text = it, color = Color.White)
                    }
                })
        }
    }
}