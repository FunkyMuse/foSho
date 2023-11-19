package dev.funkymuse.fosho.sample.features.home

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    init {
        Log.d("TopLevelDestination", "HomeViewModel init")
    }
}