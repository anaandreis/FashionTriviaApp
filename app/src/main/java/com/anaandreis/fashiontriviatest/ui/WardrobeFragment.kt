package com.anaandreis.fashiontriviatest.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anaandreis.fashiontriviatest.R
import com.anaandreis.fashiontriviatest.data.LooksfromFirebase
import com.anaandreis.fashiontriviatest.data.WardobreItem
import com.anaandreis.fashiontriviatest.data.WardrobeLookAdapter
import com.anaandreis.fashiontriviatest.databinding.FragmentWardrobeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue


class WardrobeFragment : Fragment() {

    val sharedViewModel: GameViewModel by activityViewModels()

    private lateinit var adapter: WardrobeLookAdapter
    private var wardrobeLooks = mutableListOf<WardobreItem>()
    private lateinit var recyclerView: RecyclerView

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

        recyclerView = requireView().findViewById(R.id.recyclerView)
        // Set up the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = WardrobeLookAdapter(requireContext(), wardrobeLooks)
        recyclerView.adapter = adapter

        // Load the WardrobeLooks from Firebase Firestore
    }

}