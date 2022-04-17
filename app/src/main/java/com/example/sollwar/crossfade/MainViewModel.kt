package com.example.sollwar.crossfade

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val crossFadeLiveData: MutableLiveData<CrossFade> = MutableLiveData()
    var path1: Uri? = null
    var path2: Uri? = null

    fun startCrossFade(context: Context, crossFadeValue: Int) {
        if (crossFadeLiveData.value != null) {
            crossFadeLiveData.value!!.destroyLoop()
        }
        if (path1 != null && path2 != null) {
            crossFadeLiveData.value = CrossFade(context, path1!!, path2!!, crossFadeValue)
        }
    }

}