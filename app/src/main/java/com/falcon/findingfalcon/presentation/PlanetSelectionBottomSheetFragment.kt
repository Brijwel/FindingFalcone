package com.falcon.findingfalcon.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.falcon.findingfalcon.data.network.response.Planet
import com.falcon.findingfalcon.databinding.FragmentPlanetSelectionBinding
import com.falcon.findingfalcon.presentation.adapter.PlanetAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlanetSelectionBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        private const val PLANETS = "planets"
        private const val DESTINATION_INDEX = "destination_index"
        fun getInstance(
            destinationIndex: Int,
            planets: List<Planet>,
            onSelectedCallback: (destinationIndex: Int, planet: Planet) -> Unit
        ): PlanetSelectionBottomSheetFragment {
            return PlanetSelectionBottomSheetFragment().apply {
                arguments = bundleOf(
                    PLANETS to planets,
                    DESTINATION_INDEX to destinationIndex
                )
                this.onSelectedCallback = onSelectedCallback
            }
        }
    }

    private lateinit var onSelectedCallback: (destinationIndex: Int, planet: Planet) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPlanetSelectionBinding.inflate(layoutInflater, container, false)
        val planets = requireArguments().getParcelableArrayList<Planet>(PLANETS)?.toList()
        val destinationIndex = requireArguments().getInt(DESTINATION_INDEX, -1)
        if (planets != null) {
            val adapter = PlanetAdapter(
                planets = planets,
                destinationIndex = destinationIndex
            ) { planet: Planet, reSelected: Boolean ->
                if (!reSelected)
                    onSelectedCallback(destinationIndex, planet)
                dismiss()
            }
            binding.rvPlanets.adapter = adapter
        }
        return binding.root
    }
}