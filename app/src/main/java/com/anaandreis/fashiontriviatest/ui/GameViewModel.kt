package com.anaandreis.fashiontriviatest.ui


import android.annotation.SuppressLint
import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.*
import com.anaandreis.fashiontriviatest.data.Designers
import com.anaandreis.fashiontriviatest.data.Look
import com.anaandreis.fashiontriviatest.data.Looks
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.anaandreis.fashiontriviatest.Repository.DataScore



@SuppressLint("StaticFieldLeak")

class GameViewModel(application:Application): AndroidViewModel(application){

    private val score = DataScore
    //variables


    private lateinit var currentLook: Look
    private lateinit var answers: MutableList<String>
    lateinit var correctAnswer: String

    private var usedLooks: MutableSet<Look> = mutableSetOf()
    private var unusedLooks: MutableSet<Look> = mutableSetOf()
    var currentImage = 0
    var currentQuestionNumber = 0

    private val _answersOptions = MutableLiveData<List<String>>()
    val answersOptions: LiveData<List<String>>
        get() = _answersOptions

    private val _isAnswerSelected = MutableLiveData<Boolean>(false)
    val isAnswerSelected: LiveData<Boolean>
        get() = _isAnswerSelected

    val correctQuestionNumberLiveData = MutableLiveData<Int>()
    var correctQuestionNumber = 0
        set(value) {
            field = value
            correctQuestionNumberLiveData.value = value
        }

    //initialization block

    init {

        

        for(i in 0 until Looks.size){
            unusedLooks.add(Looks[i])
        }

        randomizeQuestions()
    }


    fun randomizeQuestions() {
        Looks.shuffle()
        setQuestion()
    }


    fun setQuestion() {

        if (unusedLooks.isEmpty()) {
            // Reset the used and unused sets if all looks have been used
            usedLooks.clear()
            unusedLooks.addAll(Looks)
        }

        currentLook = unusedLooks.random()

        usedLooks.add(currentLook)
        unusedLooks.remove(currentLook)


        currentImage = currentLook.image
        Designers.shuffle()

        correctAnswer = currentLook.designer

        answers = (mutableSetOf(correctAnswer,
                Designers[0],
                Designers[1],
                Designers[2])).toMutableList()

        if (answers.size < 4) {
            answers.add(Designers.random())
        }

        _answersOptions.value = answers.shuffled()

    }


}
