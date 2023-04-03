package com.anaandreis.fashiontriviatest.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.anaandreis.fashiontriviatest.R
import com.anaandreis.fashiontriviatest.databinding.FragmentAboutBinding
import com.anaandreis.fashiontriviatest.databinding.FragmentTitleBinding



class AboutFragment : Fragment() {


    override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentAboutBinding>(
            inflater,
            R.layout.fragment_about, container, false
        )
        binding.buttonPlay.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_aboutFragment_to_homeFragment)}

        return binding.root
    }

}