package dev.funkymuse.fosho.navigator.android.wrapped

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn


fun <T> NavBackStackEntry.getResultAsStateFlow(key: String, initialValue: T? = null): StateFlow<T?> =
    savedStateHandle.getStateFlow(key, initialValue)

fun <T> NavBackStackEntry.getResult(key: String): T? =
    savedStateHandle.get<T>(key)

fun <T> NavBackStackEntry.getResultAsFlow(key: String, initialValue: T? = null): Flow<T?> =
    savedStateHandle.getLiveData(key, initialValue).asFlow()


private fun <T> LiveData<T>.asFlow(): Flow<T?> = callbackFlow {
    val observer = Observer<T> { value -> trySend(value) }
    observeForever(observer)
    awaitClose {
        removeObserver(observer)
    }
}.flowOn(Dispatchers.Main.immediate)


fun <T> NavBackStackEntry?.setResult(key: String, value: T) =
    this?.savedStateHandle?.set(key, value)

//controller

fun <T> NavHostController.setResult(key: String, value: T) =
    previousBackStackEntry?.setResult(key, value)

fun <T> NavHostController.getResult(key: String, defaultValue: T): T =
    currentBackStackEntry?.savedStateHandle?.get<T>(key) ?: defaultValue

@Composable
fun <T> NavHostController.getResultAndRemember(key: String, defaultValue: T? = null) = remember {
    getResult(key, defaultValue)
}