package com.example.ui

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundManager {
    var isSoundEnabled: Boolean = true

    fun playCorrect() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            // A cute double-chime (rising interval): C5 followed by G5
            playTone(523.25, 80) // C5
            playTone(783.99, 120) // G5
        }
    }

    fun playIncorrect() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            // A slightly low warning beep
            playTone(180.0, 250)
        }
    }

    fun playMascotTap() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            // A cute quick bubble pop sound (G4 -> C5 -> E5)
            playTone(392.00, 40)
            playTone(523.25, 40)
            playTone(659.25, 60)
        }
    }

    fun playSelectLetter() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            // A quick crisp high-pitched pop sound for selecting/adding letters
            playTone(850.0, 25)
        }
    }

    fun playDeselectLetter() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            // A quick distinct lower-pitched pop sound for deselecting/deleting letters
            playTone(450.0, 30)
        }
    }

    fun playCheer() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            // A cheerful victory arpeggio: C5 -> E5 -> G5 -> C6 (fanfare)
            launch {
                playTone(523.25, 80) // C5
                playTone(659.25, 80) // E5
                playTone(783.99, 80) // G5
                playTone(1046.50, 200) // C6
            }
            // In parallel, simulate a cute "cheer/applause" of fast mini bubble pops / handclaps
            repeat(12) { i ->
                launch {
                    kotlinx.coroutines.delay((i * 45 + (10..50).random()).toLong())
                    val freq = (600..1200).random().toDouble()
                    val duration = (40..70).random()
                    playTone(freq, duration)
                }
            }
        }
    }

    private fun playTone(frequency: Double, durationMs: Int) {
        try {
            val sampleRate = 44100
            val numSamples = (durationMs * sampleRate / 1000)
            val sample = DoubleArray(numSamples)
            val generatedSnd = ByteArray(2 * numSamples)

            // Fill with a pleasant sine wave (with simple fade out to avoid clicks)
            for (i in 0 until numSamples) {
                val fadePercent = if (i > numSamples - 1000) {
                    (numSamples - i).toDouble() / 1000.0
                } else {
                    1.0
                }
                sample[i] = sin(2 * Math.PI * i / (sampleRate / frequency)) * fadePercent
            }

            // Convert to 16 bit PCM sound array
            var idx = 0
            for (dVal in sample) {
                val valShort = (dVal * 16384).toInt().toShort() // Moderate volume
                generatedSnd[idx++] = (valShort.toInt() and 0x00ff).toByte()
                generatedSnd[idx++] = ((valShort.toInt() and 0xff00) ushr 8).toByte()
            }

            // Create AudioTrack
            val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                generatedSnd.size,
                AudioTrack.MODE_STATIC
            )
            audioTrack.write(generatedSnd, 0, generatedSnd.size)
            audioTrack.play()
            
            // Block the background coroutine until the sound is finished playing
            Thread.sleep(durationMs.toLong() + 20)
            audioTrack.stop()
            audioTrack.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
