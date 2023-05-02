package com.anaandreis.fashiontriviatest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.anaandreis.fashiontriviatest.databinding.ActivityMainBinding
import com.anaandreis.fashiontriviatest.domain.GameViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {


    private lateinit var gameViewModel: GameViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]


        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        val mAuth = FirebaseAuth.getInstance()
        gameViewModel.auth = mAuth
        gameViewModel.signInAnonymously()
        this.findNavController(R.id.myNavHostFragment)

    }

}