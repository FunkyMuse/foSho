package dev.funkymuse.fosho.sample.features.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class FavoritesViewModel @Inject constructor() : ViewModel() {

    private val _testFlow = MutableStateFlow(listOf("1", "2", "3"))
    val testFlow = _testFlow.asStateFlow()

    init {
        Log.d("TopLevelDestination", "FavoritesViewModel init")
    }
}