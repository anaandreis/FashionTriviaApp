package com.anaandreis.fashiontriviatest.ui

import kotlin.random.Random
import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anaandreis.fashiontriviatest.data.Designers
import com.anaandreis.fashiontriviatest.data.Look
import com.anaandreis.fashiontriviatest.data.Looks
import com.anaandreis.fashiontriviatest.data.MAX_NO_OF_QUESTIONS

const val MAX_NO_OF_QUESTIONS = 10
@SuppressLint("StaticFieldLeak")

class GameViewModel: ViewModel() {

    private lateinit var imageView: ImageView

    var questionIndex = 0

    private var currentImage = 0

    private lateinit var currentLook: Look

    private lateinit var answers: MutableList<String>

    var currentQuestionNumber = 0

    lateinit var correctAnswer: String

    val currentQuestionLiveData = MutableLiveData<Look>()

    val answersOptions: List<String>
        get() = answers.shuffled()

    val correctQuestionNumberLiveData = MutableLiveData<Int>()

    var correctQuestionNumber = 0
        set(value) {
            field = value
            correctQuestionNumberLiveData.value = value
        }

    private var _isAnswerSelected = MutableLiveData<Boolean>(false)
    val isAnswerSelected: LiveData<Boolean>
        get() = _isAnswerSelected

    fun selectAnswer() {
        _isAnswerSelected.value = true
    }

    init {
        // Set initial values for the order
        randomizeQuestions()
        currentImage
    }

    fun randomizeQuestions() {
        questionIndex = 0
        Looks.shuffle()
        setQuestion()
    }

    private fun setQuestion(): List<String> {
        currentLook = Looks[questionIndex]
        currentQuestionLiveData.value = currentLook
        currentImage = currentLook.image
        imageView.setImageResource(currentImage)
        Designers.shuffle()

        correctAnswer = Looks[0].designer

        answers = (mutableSetOf(correctAnswer,
            Designers[0],
            Designers[1],
            Designers[2])).toMutableList()

        if (answers.size < 4) {
            answers.add(Designers.random())
        }

        return answers

    }


}
