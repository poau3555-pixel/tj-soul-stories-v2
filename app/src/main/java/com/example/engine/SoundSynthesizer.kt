package com.example.engine

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object SoundSynthesizer {

    private const val SAMPLE_RATE = 22050
    private var isSfxEnabled = true
    private var isMusicEnabled = true
    private val scope = CoroutineScope(Dispatchers.Default)
    private var bgmJob: Job? = null

    fun setSoundFxEnabled(enabled: Boolean) {
        isSfxEnabled = enabled
    }

    fun isSoundFxEnabled(): Boolean = isSfxEnabled

    fun setMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        if (!enabled) {
            stopBackgroundMusic()
        } else {
            startBackgroundMusic()
        }
    }

    fun isMusicEnabled(): Boolean = isMusicEnabled

    fun setMuted(muted: Boolean) {
        setSoundFxEnabled(!muted)
        setMusicEnabled(!muted)
    }

    fun isMuted(): Boolean = !isSfxEnabled && !isMusicEnabled

    fun startBackgroundMusic() {
        if (!isMusicEnabled || bgmJob?.isActive == true) return

        bgmJob = scope.launch {
            // Joyful marimba pentatonic melody loop
            val melody = listOf(
                Pair(523.25, 200L), // C5
                Pair(587.33, 200L), // D5
                Pair(659.25, 200L), // E5
                Pair(783.99, 400L), // G5
                Pair(659.25, 200L), // E5
                Pair(783.99, 200L), // G5
                Pair(880.00, 400L), // A5
                Pair(1046.50, 400L), // C6
                Pair(880.00, 200L), // A5
                Pair(783.99, 200L), // G5
                Pair(659.25, 400L), // E5
                Pair(523.25, 400L)  // C5
            )

            while (isActive && isMusicEnabled) {
                for (note in melody) {
                    if (!isActive || !isMusicEnabled) break
                    playMarimbaTone(note.first, (note.second.toDouble() / 1000.0) * 0.8, 0.15)
                    delay(note.second)
                }
                delay(600) // Brief musical pause before loop repeats
            }
        }
    }

    fun stopBackgroundMusic() {
        bgmJob?.cancel()
        bgmJob = null
    }

    private fun playMarimbaTone(freq: Double, duration: Double, volume: Double) {
        val numSamples = (duration * SAMPLE_RATE).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-9.0 * t)
            val wave = 0.7 * sin(2 * PI * freq * t) + 0.3 * sin(2 * PI * (freq * 2.0) * t)
            val sample = (wave * env * volume).coerceIn(-1.0, 1.0)
            samples[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        playPcm(samples)
    }

    fun playClickSound() {
        if (!isSfxEnabled) return
        scope.launch {
            val duration = 0.04
            val numSamples = (duration * SAMPLE_RATE).toInt()
            val samples = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val env = exp(-20.0 * t)
                val wave = sin(2 * PI * 880.0 * t)
                samples[i] = ((wave * env * 0.4) * Short.MAX_VALUE).toInt().toShort()
            }
            playPcm(samples)
        }
    }

    fun playSwapSound() {
        if (!isSfxEnabled) return
        scope.launch {
            val duration = 0.08
            val numSamples = (duration * SAMPLE_RATE).toInt()
            val samples = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val freq = 400.0 + (i.toDouble() / numSamples) * 300.0
                val env = 1.0 - (i.toDouble() / numSamples)
                val sample = sin(2 * PI * freq * t) * env * 0.4
                samples[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
            playPcm(samples)
        }
    }

    fun playMatchSound(comboChain: Int = 1) {
        if (!isSfxEnabled) return
        scope.launch {
            val baseFreqs = doubleArrayOf(261.63, 293.66, 329.63, 392.00, 440.00, 523.25, 587.33, 659.25, 783.99, 1046.50)
            val freqIndex = (comboChain - 1).coerceIn(0, baseFreqs.lastIndex)
            val freq = baseFreqs[freqIndex]

            val duration = 0.16
            val numSamples = (duration * SAMPLE_RATE).toInt()
            val samples = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val env = exp(-8.0 * t)
                val wave = 0.6 * sin(2 * PI * freq * t) +
                        0.3 * sin(2 * PI * (freq * 2.0) * t) +
                        0.15 * sin(2 * PI * (freq * 3.0) * t)
                val sample = (wave * env * 0.7).coerceIn(-1.0, 1.0)
                samples[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
            playPcm(samples)
        }
    }

    fun playStripedBlast() {
        if (!isSfxEnabled) return
        scope.launch {
            val duration = 0.25
            val numSamples = (duration * SAMPLE_RATE).toInt()
            val samples = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val sweepFreq = 1200.0 - (i.toDouble() / numSamples) * 800.0
                val env = exp(-5.0 * t)
                val noise = (Math.random() * 2.0 - 1.0) * 0.2
                val wave = sin(2 * PI * sweepFreq * t) * 0.6 + noise
                val sample = (wave * env * 0.8).coerceIn(-1.0, 1.0)
                samples[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
            playPcm(samples)
        }
    }

    fun playWrappedExplosion() {
        if (!isSfxEnabled) return
        scope.launch {
            val duration = 0.35
            val numSamples = (duration * SAMPLE_RATE).toInt()
            val samples = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val env = exp(-6.0 * t)
                val lowFreq = 120.0 - (i.toDouble() / numSamples) * 60.0
                val noise = (Math.random() * 2.0 - 1.0) * 0.5
                val wave = sin(2 * PI * lowFreq * t) * 0.5 + noise * 0.5
                val sample = (wave * env * 0.9).coerceIn(-1.0, 1.0)
                samples[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
            playPcm(samples)
        }
    }

    fun playSugarLightningZap() {
        if (!isSfxEnabled) return
        scope.launch {
            val duration = 0.4
            val numSamples = (duration * SAMPLE_RATE).toInt()
            val samples = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val env = exp(-4.0 * t)
                val zapFreq = 1800.0 + sin(120.0 * t) * 600.0
                val noise = (Math.random() * 2.0 - 1.0) * 0.4
                val wave = sin(2 * PI * zapFreq * t) * 0.6 + noise
                val sample = (wave * env * 0.85).coerceIn(-1.0, 1.0)
                samples[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
            playPcm(samples)
        }
    }

    fun playColorBombSparkle() {
        if (!isSfxEnabled) return
        scope.launch {
            val duration = 0.45
            val numSamples = (duration * SAMPLE_RATE).toInt()
            val samples = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val env = exp(-3.0 * t)
                val f1 = 800.0 + sin(50.0 * t) * 300.0
                val f2 = 1200.0 + sin(80.0 * t) * 400.0
                val wave = 0.4 * sin(2 * PI * f1 * t) + 0.4 * sin(2 * PI * f2 * t)
                val sample = (wave * env * 0.7).coerceIn(-1.0, 1.0)
                samples[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
            playPcm(samples)
        }
    }

    fun playSweetComboFanfare() {
        if (!isSfxEnabled) return
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            for (freq in notes) {
                val duration = 0.1
                val numSamples = (duration * SAMPLE_RATE).toInt()
                val samples = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val env = exp(-8.0 * t)
                    val wave = 0.7 * sin(2 * PI * freq * t) + 0.3 * sin(2 * PI * freq * 2.0 * t)
                    samples[i] = ((wave * env * 0.8) * Short.MAX_VALUE).toInt().toShort()
                }
                playPcm(samples)
                delay(80)
            }
        }
    }

    fun playVictoryFanfare() {
        if (!isSfxEnabled) return
        scope.launch {
            val chord1 = doubleArrayOf(523.25, 659.25, 783.99)
            val chord2 = doubleArrayOf(698.46, 880.00, 1046.50)
            val chord3 = doubleArrayOf(783.99, 987.77, 1174.66)
            val chord4 = doubleArrayOf(1046.50, 1318.51, 1567.98)

            val sequence = listOf(chord1, chord2, chord3, chord4)
            for (chord in sequence) {
                val duration = 0.22
                val numSamples = (duration * SAMPLE_RATE).toInt()
                val samples = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val env = exp(-4.0 * t)
                    var wave = 0.0
                    for (f in chord) {
                        wave += (1.0 / chord.size) * sin(2 * PI * f * t)
                    }
                    samples[i] = ((wave * env * 0.8) * Short.MAX_VALUE).toInt().toShort()
                }
                playPcm(samples)
                delay(180)
            }
        }
    }

    fun playHammerSmash() {
        if (!isSfxEnabled) return
        scope.launch {
            val duration = 0.2
            val numSamples = (duration * SAMPLE_RATE).toInt()
            val samples = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val env = exp(-10.0 * t)
                val noise = (Math.random() * 2.0 - 1.0)
                samples[i] = ((noise * env * 0.9) * Short.MAX_VALUE).toInt().toShort()
            }
            playPcm(samples)
        }
    }

    private fun playPcm(samples: ShortArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()
            audioTrack.setNotificationMarkerPosition(samples.size)
            audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onPeriodicNotification(track: AudioTrack?) {}
                override fun onMarkerReached(track: AudioTrack?) {
                    track?.release()
                }
            })
        } catch (_: Exception) {
        }
    }
}
