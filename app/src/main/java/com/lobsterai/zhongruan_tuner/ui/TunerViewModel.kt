package com.lobsterai.zhongruan_tuner.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lobsterai.zhongruan_tuner.audio.AudioRecorder
import com.lobsterai.zhongruan_tuner.audio.PitchDetector
import com.lobsterai.zhongruan_tuner.model.RuanString
import com.lobsterai.zhongruan_tuner.model.TunerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TunerViewModel(
    private val audioRecorder: AudioRecorder,
    private val pitchDetector: PitchDetector
) : ViewModel() {

    companion object {
        private const val TAG = "TunerViewModel"
    }

    private val _state = MutableStateFlow(TunerState())
    val state: StateFlow<TunerState> = _state.asStateFlow()

    private var isListening = false

    init {
        Log.d(TAG, "ViewModel initialized")
    }

    fun requestMicrophonePermission() {
        Log.d(TAG, "Requesting microphone permission")
        if (!audioRecorder.hasPermission()) {
            _state.value = _state.value.copy(
                error = "需要麦克风权限\n请在设置中允许麦克风访问"
            )
            Log.w(TAG, "Microphone permission not granted")
        } else {
            Log.d(TAG, "Microphone permission granted")
            startListening()
        }
    }

    private fun startListening() {
        if (isListening) {
            Log.d(TAG, "Already listening, skipping")
            return
        }

        if (!audioRecorder.hasPermission()) {
            Log.w(TAG, "Cannot start: no permission")
            return
        }

        isListening = true
        Log.d(TAG, "Starting audio recording")

        viewModelScope.launch {
            try {
                audioRecorder.getAudioFlow().collect { audioData ->
                    try {
                        val frequency = pitchDetector.detectFrequency(audioData)
                        if (frequency != null) {
                            updateState(frequency)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error detecting pitch: ${e.message}")
                    }
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "Security exception: ${e.message}")
                _state.value = _state.value.copy(error = "麦克风权限被拒绝")
            } catch (e: Exception) {
                Log.e(TAG, "Audio recording error: ${e.message}")
                _state.value = _state.value.copy(error = "音频采集失败\n请检查麦克风连接")
            }
        }
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
                error = null // Clear any previous errors
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error updating state: ${e.message}")
        }
    }

    fun selectString(string: RuanString) {
        Log.d(TAG, "String selected: ${string.stringName}")
        _state.value = _state.value.copy(
            selectedString = string,
            detectedFrequency = null,
            detectedPitch = null,
            deviation = 0f,
            error = null
        )
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
        if (audioRecorder.hasPermission()) {
            startListening()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared, stopping audio recording")
        audioRecorder.stop()
    }
}

class TunerViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("TunerViewModelFactory", "Creating ViewModel")
        val audioRecorder = AudioRecorder(context)
        val pitchDetector = PitchDetector()
        return TunerViewModel(audioRecorder, pitchDetector) as T
    }
}
