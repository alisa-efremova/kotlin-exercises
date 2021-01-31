package com.example.geoquiz

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

const val EXTRA_ANSWER_SHOWN = "com.example.geoquiz.answer_shown"
private const val EXTRA_CORRECT_ANSWER = "com.example.geoquiz.correct_anwer"
private const val KEY_ANSWER_SHOWN = "answer_shown"

class CheatActivity : AppCompatActivity() {

    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button

    private var correctAnswer = false
    private var isAnswerShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        correctAnswer = intent.getBooleanExtra(EXTRA_CORRECT_ANSWER, false)

        isAnswerShown = savedInstanceState?.getBoolean(KEY_ANSWER_SHOWN) ?: false
        setAnswerShownResult(isAnswerShown)

        initViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(KEY_ANSWER_SHOWN, isAnswerShown)
    }

    private fun showAnswer() {
        isAnswerShown = true
        val answerText = when {
            correctAnswer -> R.string.button_true
            else -> R.string.button_false
        }

        answerTextView.setText(answerText)
        setAnswerShownResult(true)
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(RESULT_OK, data)
    }

    private fun initViews() {
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)

        showAnswerButton.setOnClickListener {
            showAnswer()
        }

        if (isAnswerShown) {
            showAnswer()
        }
    }

    companion object {
        fun newIntent(packageContext: Context, correctAnswer: Boolean) : Intent {
            val intent = Intent(packageContext, CheatActivity::class.java)
            intent.putExtra(EXTRA_CORRECT_ANSWER, correctAnswer)
            return intent
        }
    }
}