package com.example.geoquiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class QuizActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatBtn: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var questionCountTV: TextView
    private lateinit var quizGradeTV: TextView
    private lateinit var quizVM: QuizViewModel
    private lateinit var cheatVM: CheatViewModel

    companion object {
        const val CURRENT_INDEX = "current_index"
        const val USER_GRADE = "user_grade"
        const val START_ACT_FOR_RESULT = 101
        private const val TAG = "QuizActivity"
        const val SHOWN_ANSWER = "shown_answer"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        trueButton = findViewById(R.id.true_button)
        prevButton = findViewById(R.id.prev_button)
        cheatBtn = findViewById(R.id.cheat_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        questionCountTV = findViewById(R.id.questionCountTV)
        quizGradeTV = findViewById(R.id.quizGradeTV)


        cheatVM = ViewModelProvider(this).get(CheatViewModel::class.java)

        quizVM = ViewModelProvider(this).get(QuizViewModel::class.java)
        quizVM.setCurrentIndex(savedInstanceState?.getInt(CURRENT_INDEX) ?: 0)
        quizVM.setUserGrade(savedInstanceState?.getInt(USER_GRADE) ?: 0)
    }

    override fun onResume() {
        super.onResume()

        /*
        Quiz VM 's Index Observing From QuizActivity:
         */

        quizVM.currentIndexLiveData.observe(this) { index ->

            questionTextView.setText(quizVM.currentQuestion(index).textResId)
            prevButton.isEnabled = index != 0
            nextButton.isEnabled = index != (quizVM.questionSize - 1)

            if (quizVM.currentQuestion(index).answered) {
                trueButton.isEnabled = false
                falseButton.isEnabled = false
            } else {
                trueButton.isEnabled = true
                falseButton.isEnabled = true
            }

            questionCountTV.text = resources.getString(
                R.string.question_count_string,
                (index + 1).toString(),
                quizVM.questionSize.toString()
            )

            trueButton.setOnClickListener {
                if (quizVM.currentQuestion(index).cheated) {
                    quizVM.nextQuestion()
                    Toast.makeText(this, "You Cheater", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                quizVM.answer(true, quizVM.currentQuestion(index).answer)
            }

            falseButton.setOnClickListener {
                if (quizVM.currentQuestion(index).cheated) {
                    quizVM.nextQuestion()
                    Toast.makeText(this, "You Cheater", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                quizVM.answer(false, quizVM.currentQuestion(index).answer)
            }
        }

        /*
        QuizVM's UserGrade Observing From QuizActivity
         */

        quizVM.userGradeLiveData.observe(this) { grade ->
            quizGradeTV.text = resources.getString(
                R.string.question_count_string,
                grade.toString(),
                quizVM.questionSize.toString()
            )
            if (grade == 6) {
                Toast.makeText(this@QuizActivity, "Genius Item Unlocked!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        nextButton.setOnClickListener {
            quizVM.nextQuestion()
        }

        prevButton.setOnClickListener {
            quizVM.prevQuestion()
        }

        cheatVM.cheatTokenLiveData.observe(this@QuizActivity) { token ->
            cheatBtn.text = this@QuizActivity.resources.getString(
                R.string.question_count_string,
                token.toString(),
                "3 Cheats"
            )
            if (token > 0) {
                cheatBtn.setOnClickListener {
                    startActivityForResult(
                        CheatActivity.newIntent(
                            this@QuizActivity,
                            quizVM.currentQuestion(quizVM.currentIndexLiveData.value!!).answer
                        ), START_ACT_FOR_RESULT
                    )
                }
            } else {
                cheatBtn.isEnabled = false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putInt(CURRENT_INDEX, quizVM.currentIndexLiveData.value!!)
            putInt(USER_GRADE, quizVM.userGradeLiveData.value!!)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == START_ACT_FOR_RESULT) {
            quizVM.currentQuestion(quizVM.currentIndexLiveData.value!!).cheated =
                data?.getBooleanExtra(SHOWN_ANSWER, false) ?: false
            if (data?.getBooleanExtra(SHOWN_ANSWER, false) == true) {
                cheatVM.cheat()
            }
        }
    }
}