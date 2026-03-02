package com.lobsterai.zhongruan_tuner.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AudioRecorder(private val context: Context) {

    companion object {
        private const val TAG = "AudioRecorder"
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val BUFFER_SIZE_FACTOR = 4
    }

    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    /**
     * 检查是否有录音权限
     */
    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 获取音频数据流
     */
    fun getAudioFlow(): Flow<ShortArray> = flow {
        try {
            if (!hasPermission()) {
                Log.w(TAG, "No microphone permission")
                return@flow
            }

            val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
            if (bufferSize <= 0) {
                Log.e(TAG, "Failed to get buffer size")
                return@flow
            }

            val actualBufferSize = bufferSize * BUFFER_SIZE_FACTOR
            val audioData = ShortArray(actualBufferSize)

            // 创建 AudioRecord
            var audioRecordInstance: AudioRecord? = null
            try {
                audioRecordInstance = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    actualBufferSize
                )
            } catch (e: SecurityException) {
                Log.e(TAG, "Security exception creating AudioRecord: ${e.message}")
                return@flow
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create AudioRecord: ${e.message}")
                return@flow
            }

            // 检查 AudioRecord 是否初始化成功
            if (audioRecordInstance == null || audioRecordInstance.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord initialization failed")
                audioRecordInstance?.release()
                return@flow
            }

            audioRecord = audioRecordInstance
            val record = audioRecordInstance

            try {
                record.startRecording()
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Failed to start recording: ${e.message}")
                return@flow
            }

            isRecording = true
            Log.d(TAG, "Audio recording started")

            var emptyReadCount = 0
            while (isRecording) {
                try {
                    val readSize = record.read(audioData, 0, audioData.size)
                    if (readSize > 0) {
                        emptyReadCount = 0
                        emit(audioData.copyOf())
                    } else if (readSize == 0) {
                        emptyReadCount++
                        if (emptyReadCount > 10) {
                            Log.w(TAG, "Too many empty reads, stopping")
                            break
                        }
                        delay(10)
                    } else {
                        Log.e(TAG, "Error reading audio data: $readSize")
                        break
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error during audio recording: ${e.message}")
                    break
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Audio recording error: ${e.message}", e)
        } finally {
            Log.d(TAG, "Stopping audio recording")
            stop()
        }
    }

    /**
     * 停止录音
     */
    fun stop() {
        try {
            isRecording = false
            audioRecord?.let { record ->
                try {
                    if (record.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                        record.stop()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error stopping recording: ${e.message}")
                }
                try {
                    record.release()
                } catch (e: Exception) {
                    Log.e(TAG, "Error releasing recording: ${e.message}")
                }
            }
            audioRecord = null
        } catch (e: Exception) {
            Log.e(TAG, "Error in stop: ${e.message}")
        }
    }
}
