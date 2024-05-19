package com.example.sharego.ui.publish

import com.google.android.gms.maps.model.LatLng

interface OnLocationSelectedListener {
    fun onLocationSelected(location: LatLng)
}