package com.lobsterai.zhongruan_tuner.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

class ReferenceTonePlayer {

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val DURATION_MS = 3000L // 3 秒
        private const val AMPLITUDE = 0.5f
    }

    private var audioTrack: AudioTrack? = null

    /**
     * 播放指定频率的正弦波
     */
    suspend fun playTone(frequency: Float) = withContext(Dispatchers.IO) {
        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_FLOAT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        val numSamples = (SAMPLE_RATE * DURATION_MS / 1000).toInt()
        val samples = FloatArray(numSamples)

        for (i in 0 until numSamples) {
            samples[i] = (AMPLITUDE * sin(2 * Math.PI * frequency * i / SAMPLE_RATE)).toFloat()
        }

        audioTrack?.write(samples, 0, samples.size, AudioTrack.WRITE_BLOCKING)

        // 等待播放完成
        kotlinx.coroutines.delay(DURATION_MS)

        stop()
    }

    /**
     * 停止播放
     */
    fun stop() {
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }
}
