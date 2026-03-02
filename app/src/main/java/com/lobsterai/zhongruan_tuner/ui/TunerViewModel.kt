package com.lobsterai.zhongruan_tuner.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lobsterai.zhongruan_tuner.audio.AudioRecorder
import com.lobsterai.zhongruan_tuner.audio.PitchDetector
import com.lobsterai.zhongruan_tuner.model.RuanString
import com.lobsterai.zhongruan_tuner.model.TunerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TunerViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "TunerViewModel"
    }

    private val audioRecorder = AudioRecorder(application)
    private val pitchDetector = PitchDetector()

    private val _state = MutableStateFlow(TunerState())
    val state: StateFlow<TunerState> = _state.asStateFlow()

    private var listeningJob: Job? = null
    private var isListening = false

    init {
        Log.d(TAG, "ViewModel initialized")
    }

    fun startListening() {
        if (isListening) {
            Log.d(TAG, "Already listening")
            return
        }

        if (!audioRecorder.hasPermission()) {
            Log.w(TAG, "No permission")
            _state.value = _state.value.copy(
                error = "需要麦克风权限"
            )
            return
        }

        isListening = true
        Log.d(TAG, "Starting listening")

        listeningJob = viewModelScope.launch {
            audioRecorder.getAudioFlow()
                .catch { e ->
                    Log.e(TAG, "Audio flow error: ${e.message}")
                    _state.value = _state.value.copy(
                        error = "音频错误: ${e.message}"
                    )
                    isListening = false
                }
                .collect { audioData ->
                    try {
                        val frequency = pitchDetector.detectFrequency(audioData)
                        if (frequency != null && frequency > 0) {
                            updateState(frequency)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Detection error: ${e.message}")
                    }
                }
        }
    }

    fun stopListening() {
        Log.d(TAG, "Stopping listening")
        isListening = false
        listeningJob?.cancel()
        listeningJob = null
        audioRecorder.stop()
    }

    private fun updateState(frequency: Float) {
        try {
            val currentString = _state.value.selectedString
            val pitchName = pitchDetector.frequencyToPitchName(frequency)
            val cents = _state.value.calculateCents(frequency, currentString.frequency)
            val status = _state.value.getStatusFromCents(cents)

            _state.value = _state.value.copy(
                detectedFrequency = frequency,
                detectedPitch = pitchName,
                deviation = cents,
                status = status,
                error = null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Update state error: ${e.message}")
        }
    }

    fun selectString(string: RuanString) {
        Log.d(TAG, "Select string: ${string.stringName}")
        _state.value = _state.value.copy(
            selectedString = string,
            detectedFrequency = null,
            detectedPitch = null,
            deviation = 0f,
            status = com.lobsterai.zhongruan_tuner.model.TunerStatus.IN_TUNE,
            error = null
        )
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
        if (!isListening) {
            startListening()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared")
        stopListening()
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return TunerViewModel(application) as T
        }
    }
}