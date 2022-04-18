package com.example.sollwar.crossfade

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel(){
    private var crossFade: CrossFade? = null
    private val playedLiveData: MutableLiveData<String> = MutableLiveData()
    private val crossFadeValue: MutableLiveData<Int> = MutableLiveData()
    var path1: Uri? = null
    var path2: Uri? = null

    fun startCrossFade(context: Context, crossFadeValue: Int) {
        if (crossFade != null) {
            crossFade!!.destroyLoop()
        }
        if (path1 != null && path2 != null) {
            crossFade = CrossFade(callbacks, context, path1!!, path2!!, crossFadeValue)
        }
    }

    private val callbacks = object : CrossFade.Callbacks {
        override fun mp1Played() {
            if (playedLiveData.value != "mp1Played") playedLiveData.value = "mp1Played"
        }
        override fun mp2Played() {
            if (playedLiveData.value != "mp2Played") playedLiveData.value = "mp2Played"
        }
        override fun crossPlayed() {
            if (playedLiveData.value != "crossPlayed") playedLiveData.value = "crossPlayed"
        }
        override fun crossFadeValueChanged(newFadeValue: Int) {
            crossFadeValue.value = newFadeValue
        }
    }

    fun getPlayed(): LiveData<String> = playedLiveData
    fun getNewCrossFadeValue(): LiveData<Int> = crossFadeValue

    override fun onCleared() {
        super.onCleared()
        crossFade!!.destroyLoop()
    }
}