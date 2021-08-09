package com.example.geoquiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CheatViewModel : ViewModel() {
    private var cheatToken: MutableLiveData<Int> = MutableLiveData(3)
    val cheatTokenLiveData: LiveData<Int>
        get() = cheatToken

    private var isCheatingVal: MutableLiveData<Boolean> = MutableLiveData(false)

    fun cheat() {
        cheatToken.value = (cheatToken.value!! - 1)
    }

    fun isCheating() = isCheatingVal.value

    fun isCheatingTrue(bool: Boolean) {
        isCheatingVal.value = bool
    }

}