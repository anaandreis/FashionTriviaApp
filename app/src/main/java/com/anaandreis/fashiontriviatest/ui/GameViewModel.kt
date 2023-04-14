
package com.anaandreis.fashiontriviatest.ui


import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.*
import androidx.lifecycle.asLiveData
import com.anaandreis.fashiontriviatest.MainActivity
import com.anaandreis.fashiontriviatest.data.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
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


@Suppress("INFERRED_TYPE_VARIABLE_INTO_EMPTY_INTERSECTION_WARNING")
@SuppressLint("StaticFieldLeak")

class GameViewModel(application:Application): AndroidViewModel(application){

    val score = DataScore(application)

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

    //authentication
    var auth: FirebaseAuth
    private lateinit var user: FirebaseUser


    //List of wardrobeLooks
    val listOfLooksWardrobe = mutableListOf<WardobreItem>()

    private var usedLooks: MutableSet<LooksfromFirebase> = mutableSetOf()
    private var unusedLooks: MutableSet<LooksfromFirebase> = mutableSetOf()
    var currentId = 0
    var currentImage = ""
    var currentDescription = ""
    var currentQuestionNumber = 0

    val listOfLooksFirebase = mutableListOf<LooksfromFirebase>()

    val gson = Gson()

    private val _answersOptions = MutableLiveData<List<String>>()
    val answersOptions: LiveData<List<String>>
        get() = _answersOptions

    private val _isAnswerSelected = MutableLiveData(false)
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


        FirebaseApp.initializeApp(getApplication())
        auth = Firebase.auth

        viewModelScope.launch(Dispatchers.IO) {
            fetchLooks()
            fetchWardrobe()
        }

    }


    fun randomizeQuestions() {
        listOfLooksFirebase.shuffle()
        setQuestion()

    }


    fun addWardrobeLooks(wardobreItem: WardobreItem) {
        val wardrobeLooksRef = FirebaseDatabase.getInstance().getReference("wardrobelooks")
        val wardrobeLooksId = wardrobeLooksRef.push().key
        val wardrobeLooks = WardobreItem(wardrobeLooksId, currentImage, currentDescription)
        wardrobeLooksRef.child(wardrobeLooksId!!).setValue(wardrobeLooks)
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

    fun fetchWardrobe() {
        //getting a instance of the database
        val databaseReference = FirebaseDatabase.getInstance().getReference("wardrobelooks")
        //listening to a single read from the database
        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (lookSnapshot in dataSnapshot.children) {
                    val lookMap = lookSnapshot.getValue<HashMap<String, Any>>()
                    val lookJsonString = gson.toJson(lookMap)
                    //deserialize to turn to a LooksFromDatabaseObject
                    val look = gson.fromJson(lookJsonString, WardobreItem::class.java)
                    listOfLooksWardrobe.add(look)
                }

                // Move these calls outside of the for loop
                Log.d("WARDROBE", "size of list: ${listOfLooksFirebase.size}")
                Log.d("LISTA2", "ATÉ AQUI FOI")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value.", error.toException())
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

        currentId = currentLook.Id

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



fun signInAnonymously() {
    // [START signin_anonymously]
    auth.signInAnonymously().addOnCompleteListener(MainActivity()) { task ->
            if (task.isSuccessful) {
                //Sign in success, update UI with the signed-in user's information
                Log.d("LOADED", "signInAnonymously:success")
                user = auth.currentUser!!
                //updateUI(user)
            } else {
                //If sign in fails, display a message to the user.
                Log.w("DIDNT LOAD", "signInAnonymously:failure", task.exception)
                Toast.makeText(getApplication(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                //updateUI(null)
            }
        }

}

    fun decrementScore() {

        viewModelScope.launch(Dispatchers.IO) {
            score.readFromDataStore.first().let { currentScore ->
                if (currentScore > 9) {val updatedScore = currentScore - 10
                score.saveToDataStore(updatedScore)
                    addWardrobeLooks(WardobreItem(currentImage, currentDescription))
                  Log.d("SENT", "DONE")                }
            }
        }
    }
}


