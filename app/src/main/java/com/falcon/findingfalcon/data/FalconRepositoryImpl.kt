package com.falcon.findingfalcon.data

import com.falcon.findingfalcon.data.network.WebService
import com.falcon.findingfalcon.data.network.request.FindFalconeRequest
import com.falcon.findingfalcon.data.network.response.FindFalconeResponse
import com.falcon.findingfalcon.data.network.response.Planet
import com.falcon.findingfalcon.data.network.response.Token
import com.falcon.findingfalcon.data.network.response.Vehicle
import com.falcon.findingfalcon.domain.FalconRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class FalconRepositoryImpl(
    private val webService: WebService
) : FalconRepository {
    override suspend fun getPlanets(): Flow<Result<List<Planet>>> {
        return flow {
            try {
                emit(Result.success(webService.getPlanets()))
            } catch (e: IOException) {
                emit(Result.failure(e))
            } catch (e: HttpException) {
                emit(Result.failure(e))
            }
        }
    }

    override suspend fun getVehicles(): Flow<Result<List<Vehicle>>> {
        return flow {
            try {
                emit(Result.success(webService.getVehicles()))
            } catch (e: IOException) {
                emit(Result.failure(e))
            } catch (e: HttpException) {
                emit(Result.failure(e))
            }
        }
    }

    override suspend fun getToken(): Flow<Result<Token>> {
        return flow {
            try {
                emit(Result.success(webService.getToken()))
            } catch (e: IOException) {
                emit(Result.failure(e))
            } catch (e: HttpException) {
                emit(Result.failure(e))
            }
        }
    }

    override suspend fun findFalcone(
        token: String,
        planet_names: List<String>,
        vehicle_names: List<String>
    ): Flow<Result<FindFalconeResponse>> {
        return flow {
            try {
                val request = FindFalconeRequest(
                    token,
                    planet_names,
                    vehicle_names
                )
                emit(Result.success(webService.findFalcone(request)))
            } catch (e: IOException) {
                emit(Result.failure(e))
            } catch (e: HttpException) {
                emit(Result.failure(e))
            }
        }
    }
}