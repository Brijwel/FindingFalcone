package com.falcon.findingfalcon.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.falcon.findingfalcon.data.network.response.Planet
import com.falcon.findingfalcon.databinding.ItemSelectionBinding

class PlanetAdapter(
    private val planets: List<Planet>,
    private val destinationIndex: Int,
    private val onSelectedCallback: (planet: Planet, reSelected: Boolean) -> Unit
) : RecyclerView.Adapter<PlanetAdapter.PlanetViewHolder>() {

    inner class PlanetViewHolder(
        val binding: ItemSelectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                planets[adapterPosition].let {
                    onSelectedCallback(it, it.selectedIndex == destinationIndex)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanetViewHolder {
        return PlanetViewHolder(
            ItemSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlanetViewHolder, position: Int) {
        planets[position].let {
            holder.binding.radioButton.text = it.name
            holder.binding.rootLayout.isEnabled = it.selectedIndex == -1 ||
                    it.selectedIndex == destinationIndex
            holder.binding.radioButton.isEnabled =
                it.selectedIndex == -1 || it.selectedIndex == destinationIndex
            holder.binding.radioButton.isChecked = it.selectedIndex == destinationIndex
        }
    }

    override fun getItemCount(): Int = planets.size
}