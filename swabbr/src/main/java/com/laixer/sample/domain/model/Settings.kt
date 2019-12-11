package com.laixer.swabbr.domain.model

data class Settings(
    val private: Boolean,
    val dailyVlogRequestLimit: Int,
    val followMode: Int
)
