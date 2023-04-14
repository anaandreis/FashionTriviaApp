package com.anaandreis.fashiontriviatest.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anaandreis.fashiontriviatest.R
import com.anaandreis.fashiontriviatest.data.WardobreItem
import com.anaandreis.fashiontriviatest.data.WardrobeLookAdapter




class WardrobeFragment : Fragment() {

    private val sharedViewModel: GameViewModel by activityViewModels()

    private lateinit var adapter: WardrobeLookAdapter
    private var wardrobeLooks = mutableListOf<WardobreItem>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var noLookText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wardrobe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wardrobeLooks = sharedViewModel.listOfLooksWardrobe
        noLookText = view.findViewById(R.id.noLooksText)

        recyclerView = requireView().findViewById(R.id.recyclerView)
        // Set up the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = WardrobeLookAdapter(requireContext(), wardrobeLooks)
        recyclerView.adapter = adapter

        if (wardrobeLooks.isEmpty()){
            noLookText.text = getString(R.string.wardrobeempty)
        } else {
            noLookText.text = ""
        }

    }

}