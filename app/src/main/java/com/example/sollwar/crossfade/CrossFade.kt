package com.example.sollwar.crossfade

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CrossFade(private val callbacks: Callbacks, private val context: Context, path1: Uri, path2: Uri, var crossFade: Int) {

    interface Callbacks  {
        fun mp1Played()
        fun mp2Played()
        fun crossPlayed()
    }

    private val mp1 = MediaPlayer()
    private val mp2 = MediaPlayer()

    init {
        mp1.apply {
            setDataSource(context, path1)
            prepareAsync()
        }
        mp2.apply {
            setDataSource(context, path2)
            prepareAsync()
        }
        mp1.setOnPreparedListener {
            mp2.setOnPreparedListener {
                startLoop()
            }
        }
    }

    private var loop = true
    private var mp1FadeOff = false
    private var mp2FadeOff = true

    fun destroyLoop() {
        loop = false
        mp1.release()
        mp2.release()
    }

    private fun startLoop() {
        // Чтобы кроссфейд был не длиннее файлов
        if (crossFade > mp1.duration) crossFade = mp1.duration / 1000
        if (crossFade > mp2.duration) crossFade = mp2.duration / 1000
        loop = true
        CoroutineScope(Dispatchers.Main).launch {
            mp1.start()
            while (loop) {
                if (mp1.isPlaying && mp2.isPlaying) {
                    callbacks.crossPlayed()
                } else if (mp1.isPlaying) {
                    callbacks.mp1Played()
                } else if (mp2.isPlaying) {
                    callbacks.mp2Played()
                }
                if (mp1.duration - mp1.currentPosition <= crossFade * 1000 && !mp1FadeOff) {
                    onCrossFade(mp1, mp2, crossFade)
                    mp1FadeOff = true
                    mp2FadeOff = false
                }
                if (mp2.duration - mp2.currentPosition <= crossFade * 1000 && !mp2FadeOff) {
                    onCrossFade(mp2, mp1, crossFade)
                    mp2FadeOff = true
                    mp1FadeOff = false
                }
                delay(100)
            }
        }
    }

    private fun onCrossFade(mp1: MediaPlayer, mp2: MediaPlayer, crossFade: Int) {
        var offVolume = 1.0f
        var onVolume = 0.0f
        val delta = 1.0f / (crossFade * 10.0f)
        mp2.setVolume(onVolume, onVolume)
        mp2.start()
        CoroutineScope(Dispatchers.Main).launch {
            while (onVolume <= 1.0f) {
                mp2.setVolume(onVolume, onVolume)
                mp1.setVolume(offVolume, offVolume)
                offVolume -= delta
                onVolume += delta
                delay(100)
            }
        }
    }
}
