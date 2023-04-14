package com.anaandreis.fashiontriviatest.data



const val MAX_NO_OF_QUESTIONS = 10

// The first answer is the correct one.  We randomize the answers before showing the text.
// All questions must have four answers.  We'd want these to contain references to string
// resources so we could internationalize. (Or better yet, don't define the questions in code...)
//val Looks: MutableList<Look> = mutableListOf( Look(R.drawable.teste_bella,"Versace", "1980", "photo", 0),
  //  Look(R.drawable.versace1, "Versace", "1990", "photo", 1),
 //   Look(R.drawable.balenciaga, "Balenciaga", "1980", "photo", 2),
 //   Look(R.drawable.gucci, "Gucci", "2000", "photo", 3),
 //   Look(R.drawable.yves, "Yves", "2010", "photo", 4),
 //   Look(R.drawable.armani, "Armani", "2010", "photo", 5),
 //   Look(R.drawable.givenchy, "Givenchy", "2000,","photo",6),
 //   Look(R.drawable.mugler, "Mugler", "2000,","photo",7),
 //   Look(R.drawable.schiaparelli, "Schiaparelli", "2000,","photo",8),
 //   Look(R.drawable.prada, "Prada", "2000,","photo",9),
 //   Look(R.drawable.valentino, "Valentino", "2000,","photo",10),
 //   Look(R.drawable.balmain, "Balmain", "2000,","photo",11))



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