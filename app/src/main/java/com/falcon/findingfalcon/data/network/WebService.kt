package com.falcon.findingfalcon.data.network

import com.falcon.findingfalcon.data.network.request.FindFalconeRequest
import com.falcon.findingfalcon.data.network.response.FindFalconeResponse
import com.falcon.findingfalcon.data.network.response.Planet
import com.falcon.findingfalcon.data.network.response.Token
import com.falcon.findingfalcon.data.network.response.Vehicle
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface WebService {
    companion object {
        const val BASE_URL = "https://findfalcone.herokuapp.com/"
    }

    @GET("planets")
    suspend fun getPlanets(): List<Planet>

    @GET("vehicles")
    suspend fun getVehicles(): List<Vehicle>

    @Headers("Accept:application/json")
    @POST("token")
    suspend fun getToken(): Token

    @Headers(
        "Accept:application/json",
        "Content-Type:application/json"
    )
    @POST("find")
    suspend fun findFalcone(@Body request: FindFalconeRequest): FindFalconeResponse

}