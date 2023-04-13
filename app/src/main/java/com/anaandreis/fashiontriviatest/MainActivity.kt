package com.anaandreis.fashiontriviatest

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.anaandreis.fashiontriviatest.databinding.ActivityMainBinding
import com.anaandreis.fashiontriviatest.ui.GameViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.auth.FirebaseAuth



//import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    //private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)


        // Initialize Firebase Auth
        //auth = Firebase.auth

        fun onSupportNavigateUp(): Boolean {
            val navController = this.findNavController(R.id.myNavHostFragment)
            return navController.navigateUp() || super.onSupportNavigateUp()
        }

    }
}
   // override fun onStart() {
       // super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        //val currentUser = auth.currentUser
        //criar a função current user aqui para passar as infos para o wardrobe
        // updateUI(currentUser)

       // fun signInAnonymously() {
            // [START signin_anonymously]
         //   auth.signInAnonymously()
          //      .addOnCompleteListener(this) { task ->
          //          if (task.isSuccessful) {
          //              // Sign in success, update UI with the signed-in user's information
          //              Log.d(TAG, "signInAnonymously:success")
          //              val user = auth.currentUser
           //             //updateUI(user)
           //         } else {
           //             // If sign in fails, display a message to the user.
           //             Log.w(TAG, "signInAnonymously:failure", task.exception)
           //             Toast.makeText(
           //                 baseContext, "Authentication failed.",
            //                Toast.LENGTH_SHORT
           //             ).show()
          //              //updateUI(null)
          //          }
         //       }






