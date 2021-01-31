package com.example.geoquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider

private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var completeButton: Button
    private lateinit var cheatButton: Button
    private lateinit var cheatsLeftTextView: TextView

    private val model: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model.currentIndex = savedInstanceState?.getInt(KEY_INDEX) ?: 0

        initViews()
        updateViews()
        setOnClickListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_INDEX, model.currentIndex)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            model.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            if (model.isCheater) {
                model.cheatsLeft--
                updateCheatState()
            }
        }
    }

    private fun updateViews() {
        updateQuestion()
        updateCheatState()
    }

    private fun updateCheatState() {
        if (model.cheatsLeft == 0) {
            cheatButton.isEnabled = false
        }
        cheatsLeftTextView.text = String.format(getString(R.string.label_cheats_left), model.cheatsLeft)
    }

    private fun updateQuestion() {
        questionTextView.setText(model.currentQuestionText)
        updateAnswerButtonsState(model.isActiveQuestion())
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val isCorrect = model.setAndCheckAnswer(userAnswer)
        updateAnswerButtonsState(false)
        showAnswer(isCorrect)
        showNextQuestion()
    }

    private fun showAnswer(isCorrect: Boolean) {
        val messageResId = when {
            model.isCheater -> R.string.toast_judgment
            isCorrect -> R.string.toast_correct
            else -> R.string.toast_incorrect
        }

        val toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 50)
        toast.show()
    }

    private fun updateAnswerButtonsState(enabled: Boolean) {
        trueButton.isEnabled = enabled
        falseButton.isEnabled = enabled
    }

    private fun showNextQuestion() {
        model.moveToNext()
        updateQuestion()
    }

    private fun showPreviousQuestion() {
        model.moveToPrevious()
        updateQuestion()
    }

    private fun showFinalReview() {
        val percentage = model.correctAnswersCount  * 100 / model.questionsCount

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Result")
        builder.setMessage("Correct answers: $percentage %\n${model.correctAnswersCount}" +
                " correct from ${model.questionsCount} ")

        builder.setNeutralButton("OK") { _, _ -> }
        builder.setNegativeButton(R.string.button_reset) { _, _ -> restartQuiz() }
        builder.show()
    }

    private fun restartQuiz() {
        model.reset()
        updateViews()
    }

    private fun openCheatActivity() {
        val intent = CheatActivity.newIntent(this, model.currentCorrectAnswer)
        startActivityForResult(intent, REQUEST_CODE_CHEAT)
    }

    private fun initViews() {
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        prevButton = findViewById(R.id.prev_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        completeButton = findViewById(R.id.complete_button)
        cheatButton = findViewById(R.id.cheat_button)
        cheatsLeftTextView = findViewById(R.id.cheats_left_text_view)
    }

    private fun setOnClickListeners() {
        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            showNextQuestion()
        }

        prevButton.setOnClickListener {
            showPreviousQuestion()
        }

        completeButton.setOnClickListener {
            showFinalReview()
        }

        cheatButton.setOnClickListener {
            openCheatActivity()
        }
    }
}