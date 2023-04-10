package com.anaandreis.fashiontriviatest.data

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.anaandreis.fashiontriviatest.R
import com.anaandreis.fashiontriviatest.databinding.FragmentGameOverBinding
import com.anaandreis.fashiontriviatest.ui.GameViewModel


const val MAX_NO_OF_QUESTIONS = 10

// The first answer is the correct one.  We randomize the answers before showing the text.
// All questions must have four answers.  We'd want these to contain references to string
// resources so we could internationalize. (Or better yet, don't define the questions in code...)
val Looks: MutableList<Look> = mutableListOf()

val Designers:MutableList<String> = mutableListOf(
    "Versace",
    "Balenciaga",
    "Gucci",
    "Prada",
    "Miumiu",
    "Hermes",
    "Balmain",
    "Valentino",
    "Mugler",
    "Givenchy",
    "Armani",
    "Yohji Yamamoto",
    "Hussein Chalayan",
    "Dior"
)