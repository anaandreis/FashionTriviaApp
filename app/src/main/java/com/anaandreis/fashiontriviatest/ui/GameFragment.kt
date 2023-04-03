package com.anaandreis.fashiontriviatest.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.anaandreis.fashiontriviatest.R
import com.anaandreis.fashiontriviatest.data.MAX_NO_OF_QUESTIONS
import com.anaandreis.fashiontriviatest.databinding.FragmentGameBinding


    class GameFragment : Fragment() {

        private val sharedViewModel: GameViewModel by activityViewModels()

        private lateinit var binding: FragmentGameBinding

        private var imageView: ImageView? = null

        private var selectedButton: Button? = null


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

            binding?.apply {
                // Specify the fragment as the lifecycle owner
                lifecycleOwner = viewLifecycleOwner

                // Assign the view model to a property in the binding class
                gameViewModel = sharedViewModel

                // Assign the fragment
                gameFragment = this@GameFragment
            }
            sharedViewModel.currentQuestionLiveData.observe(viewLifecycleOwner) { look ->
                binding?.lookImageId = look.image
            }

            binding.maxNoOfQuestions = MAX_NO_OF_QUESTIONS
        }



        fun nextQuestion() {
            if (sharedViewModel.currentQuestionNumber < MAX_NO_OF_QUESTIONS - 1) {
                sharedViewModel.currentQuestionNumber++
                sharedViewModel.randomizeQuestions()

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

        fun checkAnswer(answerButton: String, button: Button) {
            val isCorrect = answerButton == sharedViewModel.correctAnswer
            if (isCorrect) {
                sharedViewModel.correctQuestionNumber++
                button.setBackgroundColor((ContextCompat.getColor(requireContext(), R.color.correct)))
            }
            else {
                button.setBackgroundColor((ContextCompat.getColor(requireContext(), R.color.wrong)))
                Toast.makeText(requireContext(), "This is a toast message", Toast.LENGTH_SHORT).show()
            }

            binding.apply {
                selectButton(Button1.takeIf { it.text == sharedViewModel.correctAnswer } ?: Button2.takeIf { it.text == sharedViewModel.correctAnswer } ?: Button3.takeIf { it.text == sharedViewModel.correctAnswer } ?: Button4.takeIf { it.text == sharedViewModel.correctAnswer }!!)
                Button1.isEnabled = false
                Button2.isEnabled = false
                Button3.isEnabled = false
                Button4.isEnabled = false
            }

        }



        }





