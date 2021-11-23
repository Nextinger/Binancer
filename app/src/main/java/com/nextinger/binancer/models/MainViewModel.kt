package com.nextinger.binancer.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nextinger.binancer.data.objects.ContentState
import com.nextinger.binancer.data.objects.enums.State

class MainViewModel: ViewModel() {

    val contentState: MutableLiveData<ContentState> = MutableLiveData()

    fun content() {
        contentState.value = ContentState(State.CONTENT)
    }

    fun loading(message: String? = null) {
        contentState.value = ContentState(State.LOADING, message)
    }

    fun noResult(message: String? = null) {
        contentState.value = ContentState(State.NO_RESULT, message)
    }

    fun error(message: String?) {
        contentState.value = ContentState(State.ERROR, message)
    }
}