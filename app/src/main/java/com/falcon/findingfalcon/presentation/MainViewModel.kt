package com.falcon.findingfalcon.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.findingfalcon.data.network.response.Planet
import com.falcon.findingfalcon.data.network.response.Vehicle
import com.falcon.findingfalcon.domain.FalconRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: FalconRepository
) : ViewModel() {

    var event = MutableSharedFlow<MainEvent>()
        private set

    var timeTaken = MutableStateFlow(0)
        private set

    private val fetchingPlanets = MutableStateFlow(false)
    private val fetchingVehicles = MutableStateFlow(false)

    val loading =
        combine(fetchingPlanets, fetchingVehicles) { (fetchingPlanets, fetchingVehicles) ->
            fetchingPlanets || fetchingVehicles
        }.stateIn(viewModelScope, SharingStarted.Lazily, false)


    var isFindingFalcon = MutableStateFlow(false)
        private set

    private var planets: MutableList<Planet> = mutableListOf()
    private var vehicles: MutableList<Vehicle> = mutableListOf()


    private val selectedPlanets = HashMap<Int, String>(HashMap(4))
    private val selectedVehicles = HashMap<String, String>(HashMap(4))


    init {
        getPlanetsAndVehicles()
    }

    fun setPlanetSelection(destinationIndex: Int, selectedPlanet: Planet) =
        viewModelScope.launch {
            val previouslySelectedPlanet = selectedPlanets[destinationIndex]
            selectedPlanets[destinationIndex] = selectedPlanet.name
            planets.forEachIndexed { index, planet ->
                if (planet.name == selectedPlanet.name) {
                    planets[index].selectedIndex = destinationIndex
                } else if (planet.selectedIndex == destinationIndex) {
                    planets[index].selectedIndex = -1
                }
            }
            if (previouslySelectedPlanet != null) {
                selectedVehicles.remove(previouslySelectedPlanet)
                vehicles.forEachIndexed { index, vehicle ->
                    if (vehicle.isSelectedForSearch(previouslySelectedPlanet)) {
                        vehicles[index].planetsToGo.remove(previouslySelectedPlanet)
                    }
                }
            }
            calculateTimeTaken()
        }


    fun setVehicleSelection(destinationPlanet: Planet, selectedVehicle: Vehicle) {
        selectedVehicles[destinationPlanet.name] = selectedVehicle.name
        vehicles.forEachIndexed { index, vehicle ->
            if (selectedVehicle.name == vehicle.name) {
                vehicles[index].planetsToGo.add(destinationPlanet.name)
            } else if (vehicle.isSelectedForSearch(destinationPlanet.name)) {
                vehicles[index].planetsToGo.remove(destinationPlanet.name)
            }
        }
        calculateTimeTaken()
    }

    fun showPlanetPicker(destinationIndex: Int) = viewModelScope.launch {
        if (isFindingFalcon.value) return@launch
        event.emit(
            MainEvent.ShowPlanetPicker(
                destinationIndex = destinationIndex,
                planets = planets
            )
        )
    }

    fun showVehiclePicker(destinationIndex: Int) = viewModelScope.launch {
        if (isFindingFalcon.value) return@launch
        val planet = planets.find { it.selectedIndex == destinationIndex }
            ?: throw Throwable("DestinationIndex $destinationIndex not selected")
        event.emit(
            MainEvent.ShowVehiclePicker(
                planet,
                vehicles
            )
        )
    }

    private fun calculateTimeTaken() = viewModelScope.launch() {
        val timeTaken = vehicles
            .filter { it.planetsToGo.isNotEmpty() }
            .map { vehicle ->
                Pair(
                    vehicle,
                    planets
                        .filter { planet ->
                            vehicle.isSelectedForSearch(planet.name)
                        }
                )
            }
            .sumOf { (vehicle, planets) ->
                planets
                    .sumOf { planet ->
                        calculateTimeToTravel(
                            planet.distance,
                            vehicle.speed
                        )
                    }
            }
        this@MainViewModel.timeTaken.emit(timeTaken)
    }

    private fun calculateTimeToTravel(
        distance: Int,
        speed: Int,
    ) = distance.div(speed)

    fun resetGame() = viewModelScope.launch {
        selectedPlanets.clear()
        selectedVehicles.clear()
        planets.forEachIndexed { index, _ ->
            planets[index].selectedIndex = -1
        }
        vehicles.forEachIndexed { index, _ ->
            vehicles[index].planetsToGo.clear()
        }
        calculateTimeTaken()
        event.emit(MainEvent.ResetGame)
    }

    private fun getPlanetsAndVehicles() = viewModelScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.IO) {
            repository.getPlanets()
                .onStart { fetchingPlanets.emit(true) }
                .onCompletion { fetchingPlanets.emit(false) }
                .collect {
                    if (it.isSuccess) {
                        planets = it.getOrDefault(mutableListOf()).toMutableList()
                    } else {
                        showException(it.exceptionOrNull())
                    }
                }
        }
        withContext(Dispatchers.IO) {
            repository.getVehicles()
                .onStart { fetchingVehicles.emit(true) }
                .onCompletion { fetchingVehicles.emit(false) }
                .collect {
                    if (it.isSuccess) {
                        vehicles = it.getOrDefault(mutableListOf()).toMutableList()
                    } else {
                        showException(it.exceptionOrNull())
                    }
                }
        }
    }

    fun findFalcon() = viewModelScope.launch(Dispatchers.IO) {
        val selectedVehiclesCount = selectedVehicles.count()
        if (selectedVehiclesCount != 4) {
            event.emit(
                MainEvent.ShowValidationMessage
            )
            return@launch
        }

        repository.getToken()
            .onStart { isFindingFalcon.emit(true) }
            .onCompletion { isFindingFalcon.emit(false) }
            .collect { tokenResult ->
                if (tokenResult.isSuccess) {
                    tokenResult.getOrNull()?.let { token ->
                        repository.findFalcone(
                            token.token,
                            selectedVehicles.map { pair -> pair.key },
                            selectedVehicles.map { pair -> pair.value }
                        )
                            .onStart { isFindingFalcon.emit(true) }
                            .onCompletion { isFindingFalcon.emit(false) }
                            .collect {
                                if (it.isSuccess) {
                                    it.getOrNull()?.also { response ->
                                        if (response.status == "success") {
                                            event.emit(
                                                MainEvent.NavigateToSuccess(
                                                    isPlanetFound = true,
                                                    timeTaken = timeTaken.value,
                                                    planetFound = response.planet_name!!
                                                )
                                            )
                                            resetGame()
                                        } else if (response.status == "false") {
                                            event.emit(
                                                MainEvent.NavigateToSuccess(
                                                    isPlanetFound = false,
                                                    timeTaken = timeTaken.value,
                                                )
                                            )
                                            resetGame()
                                        }
                                    }
                                } else {
                                    showException(it.exceptionOrNull())
                                }
                            }
                    }
                } else {
                    showException(tokenResult.exceptionOrNull())
                }
            }
    }

    private fun showException(throwable: Throwable?) = viewModelScope.launch {
        event.emit(
            MainEvent.ShowMessage(
                throwable?.message ?: "Something went wrong!"
            )
        )
    }

}

