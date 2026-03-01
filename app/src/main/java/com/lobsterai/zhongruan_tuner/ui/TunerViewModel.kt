package com.lobsterai.zhongruan_tuner.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lobsterai.zhongruan_tuner.audio.AudioRecorder
import com.lobsterai.zhongruan_tuner.audio.PitchDetector
import com.lobsterai.zhongruan_tuner.model.RuanString
import com.lobsterai.zhongruan_tuner.model.TunerState
import com.lobsterai.zhongruan_tuner.model.TunerStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TunerViewModel(
    private val audioRecorder: AudioRecorder,
    private val pitchDetector: PitchDetector
) : ViewModel() {

    private val _state = MutableStateFlow(TunerState())
    val state: StateFlow<TunerState> = _state.asStateFlow()

    init {
        startListening()
    }

    private fun startListening() {
        if (!audioRecorder.hasPermission()) {
            _state.value = _state.value.copy(error = "需要麦克风权限")
            return
        }

        viewModelScope.launch {
            audioRecorder.getAudioFlow().collect { audioData ->
                val frequency = pitchDetector.detectFrequency(audioData)
                frequency?.let { updateState(it) }
            }
        }
    }

    private fun updateState(frequency: Float) {
        val currentString = _state.value.selectedString
        val pitchName = pitchDetector.frequencyToPitchName(frequency)
        val cents = _state.value.calculateCents(frequency, currentString.frequency)
        val status = _state.value.getStatusFromCents(cents)

        _state.value = _state.value.copy(
            detectedFrequency = frequency,
            detectedPitch = pitchName,
            deviation = cents,
            status = status
        )
    }

    fun selectString(string: RuanString) {
        _state.value = _state.value.copy(selectedString = string)
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.stop()
    }
}

class TunerViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val audioRecorder = AudioRecorder(context)
        val pitchDetector = PitchDetector()
        @Suppress("UNCHECKED_CAST")
        return TunerViewModel(audioRecorder, pitchDetector) as T
    }
}
