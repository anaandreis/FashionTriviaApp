package com.anaandreis.fashiontriviatest.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.anaandreis.fashiontriviatest.R
import com.anaandreis.fashiontriviatest.data.GlideApp
import com.anaandreis.fashiontriviatest.data.MAX_NO_OF_QUESTIONS
import com.anaandreis.fashiontriviatest.databinding.FragmentGameBinding
import com.anaandreis.fashiontriviatest.domain.GameViewModel
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.engine.DiskCacheStrategy



@Suppress("DEPRECATION")
class GameFragment : Fragment() {


    private val sharedViewModel: GameViewModel by activityViewModels()

    lateinit var binding: FragmentGameBinding

    var imageView: ImageView? = null

    private var selectedButton: Button? = null

    private var heartButtonClicked = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        imageView = binding.lookImage

        binding.Button1.setOnClickListener {
            selectButton(binding.Button1)
        }
        binding.Button2.setOnClickListener {
            selectButton(binding.Button2)
        }
        binding.Button3.setOnClickListener {
            selectButton(binding.Button3)
        }
        binding.Button4.setOnClickListener {
            selectButton(binding.Button4)
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            // Assign the view model to a property in the binding class
            gameViewModel = sharedViewModel

            // Assign the fragment
            gameFragment = this@GameFragment
        }


        sharedViewModel.resultLiveData.observe(viewLifecycleOwner) { resultLiveData ->
            if (resultLiveData) {
                sharedViewModel.randomizeQuestions()
                setImage(sharedViewModel.currentImage)
            } else {
                Log.d("GLide", "Looks list is empty or null!")
            }
        }

        colorHeartButton()
        // Button click listener
        binding.heartButton.setOnClickListener {
            heartButtonClicked = true
            likeTheLook()
            colorHeartButton()
        }



        binding.maxNoOfQuestions = MAX_NO_OF_QUESTIONS
    }

    private fun setImage(currentImage: String) {
                GlideApp.with(this)
                .load(currentImage)
                    .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView!!)
    }

    fun nextQuestion() {
        if (sharedViewModel.currentQuestionNumber < MAX_NO_OF_QUESTIONS - 1) {
            sharedViewModel.currentQuestionNumber++
            sharedViewModel.randomizeQuestions()
            setImage(sharedViewModel.currentImage)
            heartButtonClicked = false
            colorHeartButton()


            binding.Button1.setBackgroundColor((ContextCompat.getColor(requireContext(), R.color.background)))
            binding.Button2.setBackgroundColor((ContextCompat.getColor(requireContext(), R.color.background)))
            binding.Button3.setBackgroundColor((ContextCompat.getColor(requireContext(), R.color.background)))
            binding.Button4.setBackgroundColor((ContextCompat.getColor(requireContext(), R.color.background)))

            // Enable all buttons
            binding.Button1.isEnabled = true
            binding.Button2.isEnabled = true
            binding.Button3.isEnabled = true
            binding.Button4.isEnabled = true

            // Set click listeners on buttons again
            binding.Button1.setOnClickListener {
                selectButton(binding.Button1)
            }
            binding.Button2.setOnClickListener {
                selectButton(binding.Button2)
            }
            binding.Button3.setOnClickListener {
                selectButton(binding.Button3)
            }
            binding.Button4.setOnClickListener {
                selectButton(binding.Button4)
            }

            binding.invalidateAll()
            binding.executePendingBindings()

        } else {
            binding.root.findNavController().navigate(R.id.action_gameFragment_to_gameOverFragment)
        }
    }


    private fun selectButton(button: Button) {
        selectedButton?.isSelected = false
        button.isSelected = true
        selectedButton = button
        binding.apply {
            Button1.isEnabled = Button1 == button || !Button1.isSelected
            Button2.isEnabled = Button2 == button || !Button2.isSelected
            Button3.isEnabled = Button3 == button || !Button3.isSelected
            Button4.isEnabled = Button4 == button || !Button4.isSelected
        }
    }

    fun checkAnswer(button: Button, selectedAnswer: String) {


        if (selectedAnswer == sharedViewModel.correctAnswer) {
            sharedViewModel.correctQuestionNumber++
            sharedViewModel.saveToDataStore(1)
            button.setBackgroundColor((ContextCompat.getColor(requireContext(), R.color.correct)))
        }
        else {
            button.setBackgroundColor((ContextCompat.getColor(requireContext(), R.color.wrong)))
            showToast(sharedViewModel.currentDescription)
        }

        binding.apply {
            selectButton(Button1.takeIf { it.text == sharedViewModel.correctAnswer } ?: Button2.takeIf { it.text == sharedViewModel.correctAnswer } ?: Button3.takeIf { it.text == sharedViewModel.correctAnswer } ?: Button4.takeIf { it.text == sharedViewModel.correctAnswer }!!)
            Button1.isEnabled = false
            Button2.isEnabled = false
            Button3.isEnabled = false
            Button4.isEnabled = false
        }

    }

    private fun showToast(text: String) {

        val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)

        // Set the position of the toast
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 80)

        val viewGroup = toast.view as ViewGroup?

        // Get the TextView of the toast
        val textView = viewGroup!!.getChildAt(0) as TextView

        // Set the text size
        textView.textSize = 18f
        textView.setLineSpacing(15f, 1.5f)
        textView.setTextColor(Color.WHITE)


        // Set the background color of toast
        val drawable = GradientDrawable()
        drawable.setColor(resources.getColor(R.color.black))
        drawable.cornerRadius = resources.getDimension(R.dimen.toast_corner_radius)
        viewGroup.background = drawable



        // Display the Toast
        toast.show()
    }


    private fun likeTheLook(){
        sharedViewModel.decrementScore()
    }


   private fun colorHeartButton() {
       sharedViewModel.readFromDataStore.observe(
           viewLifecycleOwner,
       ) { score ->
           if (heartButtonClicked && score > 9) {
               binding.heartButton.setImageResource(R.drawable.redfull)
               showToast("Look added to your wardrobe.")
            } else if (!heartButtonClicked && score > 9) {
               binding.heartButton.setImageResource(R.drawable.redborder)
            } else if (heartButtonClicked && score < 10) {
               binding.heartButton.setImageResource(R.drawable.grayheart)
               showToast("You\'re out points!")}
       }
   }




}