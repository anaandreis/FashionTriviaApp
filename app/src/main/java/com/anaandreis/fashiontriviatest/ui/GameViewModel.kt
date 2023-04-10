package com.anaandreis.fashiontriviatest.ui


import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.ImageView
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.*
import androidx.lifecycle.asLiveData
import com.anaandreis.fashiontriviatest.data.*
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@SuppressLint("StaticFieldLeak")

    class GameViewModel(application:Application): AndroidViewModel(application){


        //DataScore variables
        private val score = DataScore(application)
        val readFromDataStore = score.readFromDataStore.asLiveData()


        private lateinit var currentLook: Look
        private lateinit var answers: MutableList<String>
        lateinit var correctAnswer: String
        private var usedLooks: MutableSet<Look> = mutableSetOf()
        private var unusedLooks: MutableSet<Look> = mutableSetOf()
        var currentImage = ""
        var currentDescription = ""
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

            fetchLooks()

            randomizeQuestions()

            }

    // Firebase Storage reference object in your ViewModel that points to the folder where your images are stored.
    private fun fetchLooks() {
        val database = Firebase.database.reference
        database.child("Looks").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val looksList = mutableListOf<Look>()
                for (childSnapshot in dataSnapshot.children) {
                    val look = childSnapshot.getValue(Look::class.java)
                    looksList.add(look!!)
                }
                Looks.clear()
                Looks.addAll(looksList)

                for (i in 0 until Looks.size) {
                    unusedLooks.add(Looks[i])
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })
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

        currentDescription = currentLook.description

        currentImage = currentLook.image_url



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


    fun saveToDataStore(points: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            score.readFromDataStore.first().let { currentScore ->
                val updatedScore = currentScore + points
                score.saveToDataStore(updatedScore)
            }
        }

    fun resetForNextMatch(){
        correctQuestionNumber = 0
        currentQuestionNumber = 0
    }

    fun loadImageFromUrl(imageUrl: String, imageView: ImageView?) {
        Glide.with(imageView?.context!!)
            .load(imageUrl)
            .into(imageView!!)
    }

}
