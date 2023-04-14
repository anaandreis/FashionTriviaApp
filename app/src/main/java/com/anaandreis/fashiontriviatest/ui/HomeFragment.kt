package com.anaandreis.fashiontriviatest.ui


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.anaandreis.fashiontriviatest.R
import com.anaandreis.fashiontriviatest.databinding.FragmentGameBinding
import com.anaandreis.fashiontriviatest.databinding.FragmentHomeBinding
import com.google.android.material.color.utilities.Score.score


class HomeFragment : Fragment() {


    val sharedViewModel: GameViewModel by activityViewModels()


    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.buttonPlay.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_homeFragment_to_gameFragment); sharedViewModel.randomizeQuestions()
        }
        binding.wardrobeButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_homeFragment_to_wardrobeFragment);
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
            homeFragment = this@HomeFragment
        }


        sharedViewModel.readFromDataStore.observe(
            viewLifecycleOwner,
        ) { score ->
            Log.d("HomeFragment", "Score: $score")
            binding.scoreNumberHome.text = score.toString()
            when(score){
                in 1..40 -> {binding.StatusImage.setImageResource(R.drawable.child_friendly)
                    binding.statusResultText.text = "Fashion Baby"}
                in 41..70 -> {binding.StatusImage.setImageResource(R.drawable.menu_book)
                    binding.statusResultText.text = "Fashion Student"}
                in 71..120 -> {binding.StatusImage.setImageResource(R.drawable.settings_accessibility)
                    binding.statusResultText.text = "Fashion Savvy"}
                in 121..400 -> {binding.StatusImage.setImageResource(R.drawable.hotel_class)
                    binding.statusResultText.text = "Fashion Star"}
            }
        }

    }
}
