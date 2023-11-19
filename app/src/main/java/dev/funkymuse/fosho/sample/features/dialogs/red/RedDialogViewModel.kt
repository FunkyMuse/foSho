package dev.funkymuse.fosho.sample.features.dialogs.red

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.funkymuse.fosho.navigator.android.RedDialogViewModelArguments
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RedDialogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val redDialogViewModelArguments = RedDialogViewModelArguments(savedStateHandle)

    private val _texts = MutableStateFlow(redDialogViewModelArguments.texts)
    val texts = _texts.asStateFlow()

}