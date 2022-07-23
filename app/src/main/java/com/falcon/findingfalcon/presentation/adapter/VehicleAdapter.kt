package com.falcon.findingfalcon.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.falcon.findingfalcon.R
import com.falcon.findingfalcon.data.network.response.Planet
import com.falcon.findingfalcon.data.network.response.Vehicle
import com.falcon.findingfalcon.databinding.ItemSelectionBinding

class VehicleAdapter(
    private val vehicles: List<Vehicle>,
    private val destinationPlanet: Planet,
    private val onSelectedCallback: (vehicle: Vehicle) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    inner class VehicleViewHolder(
        val binding: ItemSelectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onSelectedCallback(vehicles[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        return VehicleViewHolder(
            ItemSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        vehicles[position].let {
            holder.binding.radioButton.text = holder.itemView.context.getString(
                R.string.vehicle_with_count,
                it.name,
                it.getRemainingCount()
            )
            holder.binding.rootLayout.isEnabled = it.canTravel(destinationPlanet.distance)
            holder.binding.radioButton.isEnabled = it.canTravel(destinationPlanet.distance)
            holder.binding.outOfRange.isVisible = it.inRange(destinationPlanet.distance).not()
                                                    && it.isAvailable()
            holder.binding.radioButton.isChecked = it.isSelectedForSearch(destinationPlanet.name)
        }
    }

    override fun getItemCount(): Int = vehicles.size
}