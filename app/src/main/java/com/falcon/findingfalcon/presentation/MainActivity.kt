package com.falcon.findingfalcon.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.falcon.findingfalcon.R
import com.falcon.findingfalcon.data.network.response.Planet
import com.falcon.findingfalcon.data.network.response.Vehicle
import com.falcon.findingfalcon.databinding.ActivityMainBinding
import com.falcon.findingfalcon.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.findFalcone.setOnClickListener { viewModel.findFalcon() }
        binding.destination1.setOnClickListener(::destinationClickListener)
        binding.destination2.setOnClickListener(::destinationClickListener)
        binding.destination3.setOnClickListener(::destinationClickListener)
        binding.destination4.setOnClickListener(::destinationClickListener)

        binding.vehicle1.setOnClickListener(::vehicleClickListener)
        binding.vehicle2.setOnClickListener(::vehicleClickListener)
        binding.vehicle3.setOnClickListener(::vehicleClickListener)
        binding.vehicle4.setOnClickListener(::vehicleClickListener)

        binding.resetGame.setOnClickListener { viewModel.resetGame() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.event
                        .collect {
                            when (it) {
                                is MainEvent.Loading -> Unit
                                is MainEvent.ShowMessage -> toast(it.message)
                                is MainEvent.ShowValidationMessage -> toast(getString(R.string.validation_message))
                                is MainEvent.ResetGame -> resetGame()
                                is MainEvent.ShowPlanetPicker -> {
                                    val bottomSheet =
                                        PlanetSelectionBottomSheetFragment.getInstance(
                                            it.destinationIndex,
                                            it.planets
                                        ) { destinationIndex: Int, planet: Planet ->
                                            viewModel.setPlanetSelection(destinationIndex, planet)
                                            setDestination(destinationIndex, planet.name)
                                        }
                                    bottomSheet.show(supportFragmentManager, "planet_picker")
                                }
                                is MainEvent.ShowVehiclePicker -> {
                                    val bottomSheet =
                                        VehicleSelectionBottomSheetFragment.getInstance(
                                            it.destinationPlanet,
                                            it.vehicles
                                        ) { destinationPlanet: Planet, vehicle: Vehicle ->
                                            viewModel.setVehicleSelection(
                                                destinationPlanet,
                                                vehicle
                                            )
                                            setVehicle(
                                                destinationPlanet.selectedIndex,
                                                vehicle.name
                                            )
                                        }
                                    bottomSheet.show(supportFragmentManager, "vehicle_picker")
                                }
                                is MainEvent.NavigateToSuccess -> {
                                    MissionResultActivity.navigate(
                                        this@MainActivity,
                                        it.isPlanetFound,
                                        it.timeTaken,
                                        it.planetFound
                                    )

                                }
                            }

                        }
                }

                launch {
                    viewModel.timeTaken
                        .collectLatest {
                            binding.timeTaken.text = getString(R.string.time_taken, it)
                        }
                }
                launch {
                    viewModel.isFindingFalcon
                        .collectLatest {
                            if (it) binding.progress.show()
                            else binding.progress.hide()
                            binding.findFalcone.isEnabled = it.not()
                            binding.resetGame.isEnabled = it.not()
                            binding.scrollView.isEnabled = it.not()
                            binding.findFalcone.text = getString(
                                if (it) R.string.finding_falcone else R.string.find_falcone
                            )
                        }
                }
                launch {
                    viewModel.loading
                        .collectLatest { loading ->
                            if (loading) binding.progress.show()
                            else binding.progress.hide()
                            binding.mainViewGroup.isVisible = loading.not()
                        }
                }
            }
        }
    }


    private fun destinationClickListener(view: View) {
        when (view.id) {
            R.id.destination1 -> viewModel.showPlanetPicker(0)
            R.id.destination2 -> viewModel.showPlanetPicker(1)
            R.id.destination3 -> viewModel.showPlanetPicker(2)
            R.id.destination4 -> viewModel.showPlanetPicker(3)
        }
    }

    private fun vehicleClickListener(view: View) {
        when (view.id) {
            R.id.vehicle1 -> viewModel.showVehiclePicker(0)
            R.id.vehicle2 -> viewModel.showVehiclePicker(1)
            R.id.vehicle3 -> viewModel.showVehiclePicker(2)
            R.id.vehicle4 -> viewModel.showVehiclePicker(3)
        }
    }

    private fun setDestination(destinationIndex: Int, planet: String) {
        when (destinationIndex) {
            0 -> {
                binding.destination1.text = planet
                binding.vehicle1.isVisible = true
                binding.vehicle1.text = getString(R.string.select_vehicle)
            }
            1 -> {
                binding.destination2.text = planet
                binding.vehicle2.isVisible = true
                binding.vehicle2.text = getString(R.string.select_vehicle)
            }
            2 -> {
                binding.destination3.text = planet
                binding.vehicle3.isVisible = true
                binding.vehicle3.text = getString(R.string.select_vehicle)
            }
            3 -> {
                binding.destination4.text = planet
                binding.vehicle4.isVisible = true
                binding.vehicle4.text = getString(R.string.select_vehicle)
            }
        }
    }

    private fun setVehicle(destinationIndex: Int, vehicle: String) {
        when (destinationIndex) {
            0 -> {
                binding.vehicle1.text = vehicle
            }
            1 -> {
                binding.vehicle2.text = vehicle
            }
            2 -> {
                binding.vehicle3.text = vehicle
            }
            3 -> {
                binding.vehicle4.text = vehicle
            }
        }
    }

    private fun resetGame() {
        binding.destination1.text = getString(R.string.select_planet)
        binding.vehicle1.text = getString(R.string.select_vehicle)
        binding.vehicle1.isVisible = false
        binding.destination2.text = getString(R.string.select_planet)
        binding.vehicle2.text = getString(R.string.select_vehicle)
        binding.vehicle2.isVisible = false
        binding.destination3.text = getString(R.string.select_planet)
        binding.vehicle3.text = getString(R.string.select_vehicle)
        binding.vehicle3.isVisible = false
        binding.destination4.text = getString(R.string.select_planet)
        binding.vehicle4.text = getString(R.string.select_vehicle)
        binding.vehicle4.isVisible = false
    }
}