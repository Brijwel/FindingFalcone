package com.falcon.findingfalcon.utils

import android.app.Activity
import android.widget.Toast


fun Activity.toast(
    string: String
)= Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
