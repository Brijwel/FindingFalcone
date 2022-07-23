package com.falcon.findingfalcon.data.network.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Vehicle(
    val max_distance: Int,
    val name: String,
    val speed: Int,
    val total_no: Int,
    var planetsToGo: MutableSet<String> = mutableSetOf()
) : Parcelable {

    fun canTravel(distanceToTravel: Int): Boolean {
        return isAvailable() && inRange(distanceToTravel)
    }

    fun isAvailable(): Boolean {
        return total_no - planetsToGo.size > 0
    }

    fun inRange(distanceToTravel: Int): Boolean {
        return max_distance >= distanceToTravel
    }

    fun isSelectedForSearch(planet: String): Boolean {
        return planetsToGo.contains(planet)
    }

    fun getRemainingCount(): Int = total_no - planetsToGo.size
}