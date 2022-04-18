package com.example.sollwar.crossfade

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.*

class CrossFade(private val callbacks: Callbacks, private val context: Context, path1: Uri, path2: Uri, var crossFade: Int) {

    interface Callbacks  {
        fun mp1Played()
        fun mp2Played()
        fun crossPlayed()
        fun crossFadeValueChanged(newFadeValue: Int)
    }

    private val mp1 = MediaPlayer()
    private val mp2 = MediaPlayer()

    private var loop = true

    init {
        crossFade *= 1000
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

    fun destroyLoop() {
        loop = false
        mp1.release()
        mp2.release()
    }

    private fun startLoop() {
        // Кроссфейд не может быть больше половины трека, иначе какая-то рекурсия
        if (mp1.duration >= mp2.duration) {
            if (crossFade >= mp2.duration/2) {
                crossFade = mp2.duration/2
                callbacks.crossFadeValueChanged(crossFade/1000)
            }
        } else {
            if (crossFade >= mp1.duration/2) {
                crossFade = mp1.duration/2
                callbacks.crossFadeValueChanged(crossFade/1000)
            }
        }
        loop = true
        CoroutineScope(Dispatchers.Main).launch {
            mp1.start()
            while (loop) {
                if (mp1.isPlaying) {
                    callbacks.mp1Played()
                } else if (mp2.isPlaying) {
                    callbacks.mp2Played()
                }
                if (mp1.duration - mp1.currentPosition <= crossFade && !mp2.isPlaying) {
                    callbacks.crossPlayed()
                    val onFade = CoroutineScope(Dispatchers.IO).async {
                        return@async onCrossFade(mp1, mp2, crossFade)
                    }
                    onFade.await()
                }
                if (mp2.duration - mp2.currentPosition <= crossFade && !mp1.isPlaying) {
                    callbacks.crossPlayed()
                    val onFade = CoroutineScope(Dispatchers.IO).async {
                        return@async onCrossFade(mp2, mp1, crossFade)
                    }
                    onFade.await()
                }
                delay(100)
            }
        }
    }

    private suspend fun onCrossFade(mp1: MediaPlayer, mp2: MediaPlayer, crossFade: Int) {
        var offVolume = 1.0f
        var onVolume = 0.0f
        val delta = 1.0f / (crossFade / 100.0f)
        Log.d("CrossFade", delta.toString())
        mp2.setVolume(onVolume, onVolume)
        mp2.start()
        while (onVolume <= 1.0f && offVolume >= 0.0f) {
            mp2.setVolume(onVolume, onVolume)
            mp1.setVolume(offVolume, offVolume)
            offVolume -= delta
            onVolume += delta
            delay(100)
        }
    }
}
