
package com.anaandreis.fashiontriviatest.ui


import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.*
import androidx.lifecycle.asLiveData
import com.anaandreis.fashiontriviatest.data.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@SuppressLint("StaticFieldLeak")

class GameViewModel(application:Application): AndroidViewModel(application){

    private val score = DataScore(application)

    val readFromDataStore = score.readFromDataStore.asLiveData()
    //variables

    fun saveToDataStore(points: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            score.readFromDataStore.first().let { currentScore ->
                val updatedScore = currentScore + points
                score.saveToDataStore(updatedScore)
            }
        }


    //private lateinit var currentLook: Look
    private lateinit var currentLook: LooksfromFirebase
    private lateinit var answers: MutableList<String>
    lateinit var correctAnswer: String

    private var usedLooks: MutableSet<LooksfromFirebase> = mutableSetOf()
    private var unusedLooks: MutableSet<LooksfromFirebase> = mutableSetOf()
    var currentImage = ""
    var currentDescription = ""
    var currentQuestionNumber = 0

    val listOfLooksFirebase = mutableListOf<LooksfromFirebase>()

    val gson = Gson()

    private val _answersOptions = MutableLiveData<List<String>>()
    val answersOptions: LiveData<List<String>>
        get() = _answersOptions

    private val _isAnswerSelected = MutableLiveData<Boolean>(false)
    val isAnswerSelected: LiveData<Boolean>
        get() = _isAnswerSelected

    //the boolean that shows if the fetch looks is loaded or not
    val resultLiveData = MutableLiveData<Boolean>()

    val correctQuestionNumberLiveData = MutableLiveData<Int>()
    var correctQuestionNumber = 0
        set(value) {
            field = value
            correctQuestionNumberLiveData.value = value
        }

    //initialization block

    init {

        viewModelScope.launch(Dispatchers.IO) {
            fetchLooks()
        }

    }


    fun randomizeQuestions() {
        listOfLooksFirebase.shuffle()
        setQuestion()

    }


    // Firebase Storage reference object in your ViewModel that points to the folder where your images are stored.
    fun fetchLooks() {
        // enables offline persistence, which allows the app to store and access data from the local device cache when the app is offline.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        //getting a instance of the database
        val databaseReference = FirebaseDatabase.getInstance().getReference("looks")
        //listening to a single read from the database
        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (lookSnapshot in dataSnapshot.children) {
                    val lookMap = lookSnapshot.getValue<HashMap<String, Any>>()
                    val lookJsonString = gson.toJson(lookMap)
                    //deserialize to turn to a LooksFromDatabaseObject
                    val look = gson.fromJson(lookJsonString, LooksfromFirebase::class.java)
                    listOfLooksFirebase.add(look)
                }

                // Move these calls outside of the for loop
                Log.d("TAMANHO", "size of list: ${listOfLooksFirebase.size}")
                Log.d("LISTA", "ATÉ AQUI FOI")
                resultLiveData.value = true
                }



            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value.", error.toException())
                resultLiveData.value = false
            }
        })

    }



    fun setQuestion() {

        if (unusedLooks.isEmpty()) {
            // Reset the used and unused sets if all looks have been used
            usedLooks.clear()
            unusedLooks.addAll(listOfLooksFirebase)
            return
        }


        //currentLook = unusedLooks.random()
        currentLook = listOfLooksFirebase.random()

        usedLooks.add(currentLook)
        unusedLooks.remove(currentLook)


       // currentImage = currentLook.image
        Designers.shuffle()

        correctAnswer = currentLook.designer

        currentImage = currentLook.image_url

        currentDescription = currentLook.description

        answers = (mutableSetOf(correctAnswer,
            Designers[0],
            Designers[1],
            Designers[2])).toMutableList()

        if (answers.size < 4) {
            answers.add(Designers.random())
        }

        _answersOptions.value = answers.shuffled()

    }
    fun resetForNextMatch(){
        correctQuestionNumber = 0
        currentQuestionNumber = 0
    }

}
