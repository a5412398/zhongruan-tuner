package com.lobsterai.zhongruan_tuner.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive

class AudioRecorder(private val context: Context) {

    companion object {
        private const val TAG = "AudioRecorder"
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    @Volatile
    private var isRecording = false
    
    @Volatile
    private var audioRecord: AudioRecord? = null

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getAudioFlow(): Flow<ShortArray> = flow {
        if (!hasPermission()) {
            Log.w(TAG, "No microphone permission")
            return@flow
        }

        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        if (bufferSize <= 0) {
            Log.e(TAG, "Invalid buffer size: $bufferSize")
            return@flow
        }

        val actualBufferSize = bufferSize * 2
        val audioData = ShortArray(actualBufferSize)

        var record: AudioRecord? = null
        try {
            record = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                actualBufferSize
            )

            if (record.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord not initialized, state: ${record.state}")
                record.release()
                return@flow
            }

            audioRecord = record
            isRecording = true
            
            record.startRecording()
            Log.d(TAG, "Recording started")

            while (isRecording && isActive) {
                val readSize = record.read(audioData, 0, audioData.size)
                if (readSize > 0) {
                    emit(audioData.copyOfRange(0, readSize))
                } else if (readSize < 0) {
                    Log.e(TAG, "Read error: $readSize")
                    break
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
        } finally {
            isRecording = false
            try {
                record?.apply {
                    if (state == AudioRecord.STATE_INITIALIZED) {
                        try {
                            stop()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error stopping: ${e.message}")
                        }
                    }
                    release()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error releasing: ${e.message}")
            }
            audioRecord = null
            Log.d(TAG, "Recording stopped")
        }
    }.flowOn(Dispatchers.IO)

    fun stop() {
        Log.d(TAG, "stop() called")
        isRecording = false
        audioRecord?.let { record ->
            try {
                if (record.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    record.stop()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping record: ${e.message}")
            }
        }
    }
}