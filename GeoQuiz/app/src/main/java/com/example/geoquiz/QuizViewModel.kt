package com.example.geoquiz

import androidx.lifecycle.ViewModel

private const val initialIndex = 0
private const val initialCheatsCount = 3

class QuizViewModel : ViewModel() {

    var currentIndex = initialIndex
    var isCheater = false
    var cheatsLeft = initialCheatsCount

    val currentQuestionText: Int
        get() = questions[currentIndex].textResId

    val currentCorrectAnswer: Boolean
        get() = questions[currentIndex].answer

    val questionsCount: Int
        get() = questions.size

    val correctAnswersCount: Int
        get() = answers.sumOf { it ?: 0 }

    private val questions = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    private val answers = MutableList<Int?>(questions.size) { null }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questions.size
        isCheater = false
    }

    fun moveToPrevious() {
        currentIndex = (currentIndex - 1) % questions.size
        if (currentIndex < 0) {
            currentIndex = questions.size - 1
        }
        isCheater = false
    }

    fun setAndCheckAnswer(userAnswer: Boolean) : Boolean {
        val isCorrect = if (isCheater) {
            false
        } else {
            questions[currentIndex].answer == userAnswer
        }
        answers[currentIndex] = if (isCorrect) 1 else 0

        return isCorrect
    }

    fun isActiveQuestion() : Boolean {
        return answers[currentIndex] == null
    }

    fun reset() {
        currentIndex = initialIndex
        answers.fill(null)
        isCheater = false
        cheatsLeft = initialCheatsCount
    }

}