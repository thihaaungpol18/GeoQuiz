package com.example.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class CheatActivity : AppCompatActivity() {
    private var answer = ""
    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var cheatVM: CheatViewModel
    private lateinit var apiLevelTextView: TextView

    companion object {
        private const val TAG = "CheatActivity"
        const val ANSWER_OF_THE_Q = "answer_of_the_question"
        const val SHOWN_ANSWER = "shown_answer"

        fun newIntent(context: Context, answer: Boolean): Intent {
            return Intent(context, CheatActivity::class.java).also {
                it.putExtra(ANSWER_OF_THE_Q, answer)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        answer = intent.extras?.get(ANSWER_OF_THE_Q).toString()
        answerTextView = findViewById(R.id.answerTextView)
        apiLevelTextView = findViewById(R.id.apiLevelTextView)
        showAnswerButton = findViewById(R.id.showAnswerButton)

        cheatVM = ViewModelProvider(this).get(CheatViewModel::class.java)

        savedInstanceState?.getBoolean("OnCheatOnCheat")?.let {
            if (it) {
                cheatVM.isCheatingTrue(true)
                apiLevelTextView.text = "Cheated"
            }
        }

        showAnswerButton.setOnClickListener {
            answerTextView.text = "Answer:$answer"
            cheatVM.isCheatingTrue(true)
            setAnswerForResult()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("OnCheatOnCheat", cheatVM.isCheating()!!)
    }

    private fun setAnswerForResult() {
        val data = Intent().apply { putExtra(SHOWN_ANSWER, cheatVM.isCheating()!!) }
        setResult(Activity.RESULT_OK, data)
    }
}