package com.pixel.composeexperiments

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform