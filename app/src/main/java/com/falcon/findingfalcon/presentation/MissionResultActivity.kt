package com.falcon.findingfalcon.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.falcon.findingfalcon.R
import com.falcon.findingfalcon.databinding.ActivityMissionResultBinding

class MissionResultActivity : AppCompatActivity() {

    companion object {
        private const val TIME_TAKEN = "time_taken"
        private const val PLANET_FOUND = "planet_found"
        private const val IS_PLANET_FOUND = "is_planet_found"
        fun navigate(
            from: Context,
            isPlanetFound: Boolean,
            timeTaken: Int,
            planetFound: String? = "",
        ) {
            val intent = Intent(from, MissionResultActivity::class.java)
            intent.putExtra(TIME_TAKEN, timeTaken)
            intent.putExtra(PLANET_FOUND, planetFound)
            intent.putExtra(IS_PLANET_FOUND, isPlanetFound)
            from.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMissionResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val isPlanetFound = intent.getBooleanExtra(IS_PLANET_FOUND, false)
        if (isPlanetFound) {
            val timeTaken = intent.getIntExtra(TIME_TAKEN, 0)
            val planetFound = intent.getStringExtra(PLANET_FOUND)
            binding.timeTaken.text = getString(R.string.time_taken, timeTaken)
            binding.planetFound.text = getString(R.string.planet_found, planetFound)
            binding.message.text = getString(R.string.congratulations_message)
        } else {
            binding.message.text = getString(R.string.failed_message)
        }
        binding.planetFound.isVisible = isPlanetFound
        binding.startAgain.text = getString(
            if (isPlanetFound) R.string.start_again else R.string.try_again
        )
        binding.startAgain.setOnClickListener { finish() }

    }
}