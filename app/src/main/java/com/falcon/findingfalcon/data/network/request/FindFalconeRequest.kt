package com.falcon.findingfalcon.data.network.request

data class FindFalconeRequest(
    var token: String,
    var planet_names: List<String>,
    var vehicle_names: List<String>
)