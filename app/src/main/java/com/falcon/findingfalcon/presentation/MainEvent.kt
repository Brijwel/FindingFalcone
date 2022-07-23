package com.falcon.findingfalcon.presentation

import com.falcon.findingfalcon.data.network.response.Planet
import com.falcon.findingfalcon.data.network.response.Vehicle

sealed class MainEvent {
    data class Loading(val loading: Boolean) : MainEvent()
    data class ShowMessage(val message: String) : MainEvent()
    object ShowValidationMessage : MainEvent()
    object ResetGame : MainEvent()
    data class ShowPlanetPicker(
        val destinationIndex: Int,
        val planets: List<Planet>
    ) : MainEvent()

    data class ShowVehiclePicker(
        val destinationPlanet: Planet,
        val vehicles: List<Vehicle>
    ) : MainEvent()

    data class NavigateToSuccess(
        val isPlanetFound: Boolean,
        val timeTaken: Int,
        val planetFound: String? = ""
    ) : MainEvent()
}