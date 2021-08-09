package com.example.geoquiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    val questionSize = questionBank.size

    private var currentIndex: MutableLiveData<Int> = MutableLiveData()
    val currentIndexLiveData: LiveData<Int>
        get() = currentIndex

    private var userGrade: MutableLiveData<Int> = MutableLiveData()
    val userGradeLiveData: LiveData<Int>
        get() = userGrade

    fun nextQuestion() {
        currentIndex.value = (currentIndex.value?.plus(1))?.rem(questionBank.size)
    }

    fun currentQuestion(index: Int): Question = questionBank[index]

    fun prevQuestion() {
        currentIndex.value = (currentIndex.value?.minus(1))?.rem(questionBank.size)
    }

    fun setCurrentIndex(i: Int) {
        currentIndex.postValue(i)
    }

    fun setUserGrade(i: Int) {
        userGrade.postValue(i)
    }

    fun answer(userAnswer: Boolean, realAnswer: Boolean) {
        if (userAnswer == realAnswer) {
            userGrade.value = userGrade.value?.plus(1)
        }
        questionBank[currentIndex.value!!].answered = true
        nextQuestion()
    }


}