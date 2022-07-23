package com.falcon.findingfalcon.data.network.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Planet(
    val distance: Int,
    val name: String,
    var selectedIndex: Int = -1,
) : Parcelable