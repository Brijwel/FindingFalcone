package com.falcon.findingfalcon.domain

import com.falcon.findingfalcon.data.network.response.FindFalconeResponse
import com.falcon.findingfalcon.data.network.response.Planet
import com.falcon.findingfalcon.data.network.response.Token
import com.falcon.findingfalcon.data.network.response.Vehicle
import kotlinx.coroutines.flow.Flow


interface FalconRepository {

    suspend fun getPlanets(): Flow<Result<List<Planet>>>

    suspend fun getVehicles(): Flow<Result<List<Vehicle>>>

    suspend fun getToken(): Flow<Result<Token>>

    suspend fun findFalcone(
        token: String,
        planet_names: List<String>,
        vehicle_names: List<String>
    ): Flow<Result<FindFalconeResponse>>
}