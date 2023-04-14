package com.anaandreis.fashiontriviatest.data

import com.google.firebase.database.Exclude

data class WardobreItem (
    @get:Exclude
    var id: String? = null,
    var imageUrl : String? = null,
    val description : String? = null){
}