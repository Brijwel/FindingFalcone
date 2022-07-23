package com.falcon.findingfalcon.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.falcon.findingfalcon.data.network.response.Planet
import com.falcon.findingfalcon.data.network.response.Vehicle
import com.falcon.findingfalcon.databinding.FragmentVehicleSelectionBinding
import com.falcon.findingfalcon.presentation.adapter.VehicleAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VehicleSelectionBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        private const val VEHICLES = "vehicles"
        private const val DESTINATION_PLANET = "destination_planet"
        fun getInstance(
            destinationPlanet: Planet,
            vehicles: List<Vehicle>,
            onSelectedCallback: (destinationPlanet: Planet, vehicle: Vehicle) -> Unit
        ): VehicleSelectionBottomSheetFragment {
            return VehicleSelectionBottomSheetFragment().apply {
                arguments = bundleOf(
                    VEHICLES to vehicles,
                    DESTINATION_PLANET to destinationPlanet
                )
                this.onSelectedCallback = onSelectedCallback
            }
        }
    }

    private lateinit var onSelectedCallback: (destinationPlanet: Planet, vehicle: Vehicle) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentVehicleSelectionBinding.inflate(layoutInflater, container, false)
        val vehicles = requireArguments().getParcelableArrayList<Vehicle>(VEHICLES)?.toList()
        val destinationPlanet = requireArguments().getParcelable<Planet>(DESTINATION_PLANET)
        if (vehicles != null && destinationPlanet != null) {
            val adapter =
                VehicleAdapter(vehicles = vehicles, destinationPlanet = destinationPlanet) {
                    onSelectedCallback(destinationPlanet, it)
                    dismiss()
                }
            binding.rvVehicle.adapter = adapter
        }
        return binding.root
    }
}