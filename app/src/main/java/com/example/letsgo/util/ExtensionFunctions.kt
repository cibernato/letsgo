package com.example.letsgo.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun distancia(lat1:Double,lon1:Double,lat2:Double,lon2:Double): Double {
    val r = 6371e3; // metres
    val p1 = lat1 * Math.PI/180; // φ, λ in radians
    val p2 = lat2 * Math.PI/180;
    val a1 = (lat2-lat1) * Math.PI/180;
    val a2 = (lon2-lon1) * Math.PI/180;

    val a = sin(a1/2) * sin(a1/2) +
            cos(p1) * cos(p2) *
            sin(a2/2) * sin(a2/2);
    val c = 2 * atan2(sqrt(a), sqrt(1-a));

    return r * c; // in metres
}