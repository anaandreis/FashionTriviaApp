package com.anaandreis.fashiontriviatest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.anaandreis.fashiontriviatest.R
import com.anaandreis.fashiontriviatest.databinding.FragmentHomeBinding



class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home, container, false)

        binding.buttonPlay.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_homeFragment_to_gameFragment)}
        return binding.root
    }
    }
