package com.anaandreis.fashiontriviatest.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.anaandreis.fashiontriviatest.R
import com.anaandreis.fashiontriviatest.databinding.FragmentGameOverBinding


class GameOverFragment : Fragment() {

    private val sharedViewModel: GameViewModel by activityViewModels()

    private lateinit var binding: FragmentGameOverBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<FragmentGameOverBinding>(
            inflater,
            R.layout.fragment_game_over, container, false
        )
        binding.buttonPlay.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_gameOverFragment_to_gameFragment)}

        binding.homeButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_gameOverFragment_to_homeFragment)
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
            gameOverFragment = this@GameOverFragment
        }

        sharedViewModel.correctQuestionNumberLiveData.observe(viewLifecycleOwner) { value ->
            //Update UI with the new value
            this.binding.scoreNumberHome.text = "$value"
        }
    }
    }



