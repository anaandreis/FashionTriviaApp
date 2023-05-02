
package com.anaandreis.fashiontriviatest.domain


import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.lifecycle.asLiveData
import com.anaandreis.fashiontriviatest.MainActivity
import com.anaandreis.fashiontriviatest.data.Repository.DataScore
import com.anaandreis.fashiontriviatest.data.*
import com.anaandreis.fashiontriviatest.data.models.Designers
import com.anaandreis.fashiontriviatest.data.models.LooksfromFirebase
import com.anaandreis.fashiontriviatest.data.models.WardrobeItem
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class GameViewModel(application:Application): AndroidViewModel(application){


    //DataStore variable retrieval and update
    val score = DataScore(application)
    val readFromDataStore = score.readFromDataStore.asLiveData()
    fun saveToDataStore(points: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            score.readFromDataStore.first().let { currentScore ->
                val updatedScore = currentScore + points
                score.saveToDataStore(updatedScore)
            }
        }

    //Firebase authentication
    var auth: FirebaseAuth
    private lateinit var user: FirebaseUser


    //Gson
    val gson = Gson()


    //GameFragment variables
    private var currentId = 0
    var currentImage = ""
    var currentDescription = ""
    var currentQuestionNumber = 0
    private lateinit var currentLook: LooksfromFirebase

    private lateinit var answers: MutableList<String>
    lateinit var correctAnswer: String

    val listOfLooksWardrobe = mutableListOf<WardrobeItem>()
    val listOfLooksFirebase = mutableListOf<LooksfromFirebase>()

    private var usedLooks: MutableSet<LooksfromFirebase> = mutableSetOf()
    private var unusedLooks: MutableSet<LooksfromFirebase> = mutableSetOf()
    private var displayedItemIds = mutableSetOf<String>()


    //Livedata to feed GameFragment
    private val _answersOptions = MutableLiveData<List<String>>()
    val answersOptions: LiveData<List<String>>
        get() = _answersOptions

    private val _isAnswerSelected = MutableLiveData(false)
    val isAnswerSelected: LiveData<Boolean>
        get() = _isAnswerSelected

    //Boolean that shows if the fetch looks is loaded or not
    val resultLiveData = MutableLiveData<Boolean>()

    val correctQuestionNumberLiveData = MutableLiveData<Int>()
    var correctQuestionNumber = 0
        set(value) {
            field = value
            correctQuestionNumberLiveData.value = value
        }



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


    private fun addWardrobeLooks() {
        val wardrobeLooksRef = FirebaseDatabase.getInstance().getReference("wardrobelooks")
        val wardrobeLooksId = wardrobeLooksRef.push().key
        val wardrobeLooks = WardrobeItem(wardrobeLooksId, currentImage, currentDescription)
        wardrobeLooksRef.child(wardrobeLooksId!!).setValue(wardrobeLooks)
    }


    // Firebase Storage reference object in your ViewModel that points to the folder where your images are stored.
    private fun fetchLooks() {
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
                Log.d("FromDatabaseSize", "size of list: ${listOfLooksFirebase.size}")
                resultLiveData.value = true
                }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value.", error.toException())
                resultLiveData.value = false
            }
        })

    }

    private fun fetchWardrobe() {
        //getting a instance of the database
        val databaseReference = FirebaseDatabase.getInstance().getReference("wardrobelooks")
        //listening to a single read from the database
        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (lookSnapshot in dataSnapshot.children) {
                    val lookMap = lookSnapshot.getValue<HashMap<String, Any>>()
                    val lookJsonString = gson.toJson(lookMap)
                    //deserialize to turn to a LooksFromDatabaseObject
                    val look = gson.fromJson(lookJsonString, WardrobeItem::class.java)
                    look.id = lookSnapshot.key
                    if(!displayedItemIds.contains(look.id)) {
                        look.id?.let { displayedItemIds.add(it) }
                        listOfLooksWardrobe.add(look)}
                }

                // Move these calls outside of the for loop
                Log.d("WARDROBE", "size of list: ${listOfLooksFirebase.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })

    }


    private fun setQuestion() {

        if (unusedLooks.isEmpty()) {
            // Reset the used and unused sets if all looks have been used
            usedLooks.clear()
            unusedLooks.addAll(listOfLooksFirebase)
            return
        }

        //currentLook = unusedLooks.random()
        currentLook = unusedLooks.random()

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
                    addWardrobeLooks()
                  Log.d("SENT", "DONE")                }
            }
        }
    }
}


