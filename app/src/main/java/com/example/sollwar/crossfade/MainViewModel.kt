package com.example.sollwar.crossfade

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val crossFadeLiveData: MutableLiveData<CrossFade> = MutableLiveData()
    var path1 = ""
    var path2 = ""

    fun startCrossFade(crossFadeValue: Int) {
        if (crossFadeLiveData.value != null) {
            crossFadeLiveData.value!!.destroyLoop()
        }
        if (path1 != "" && path2 != "") {
            crossFadeLiveData.value = CrossFade(path1, path2, crossFadeValue)
            Log.d("ViewModel", "Start")
        }
    }

}