package com.laixer.swabbr.utils

import android.net.Uri
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 *  Builds a Moshi object with custom adapters. These are required
 *  for proper json parsing throughout the application.
 */
fun Moshi.Builder.BuildWithCustomAdapters(): Moshi = Moshi
    .Builder()
    .add(UuidJsonAdapter())
    .add(ZonedDateTimeAdapter())
    .add(ZoneOffsetAdapter())
    .add(UriAdapter())
    .build()

/**
 *  Adapter for [UUID] values.
 */
class UuidJsonAdapter {
    @ToJson
    fun toJson(value: UUID?) = value?.toString()

    @FromJson
    fun fromJson(input: String) = UUID.fromString(input)
}

/**
 *  Adapter for [ZonedDateTime] values.
 */
class ZonedDateTimeAdapter {
    @ToJson
    fun toJson(value: ZonedDateTime?) = value?.toString()

    @FromJson
    fun fromJson(input: String) = ZonedDateTime.parse(input)
}

/**
 *  Adapter for [Uri] values.
 */
class UriAdapter {
    @ToJson
    fun toJson(value: Uri?) = value?.toString()

    @FromJson
    fun fromJson(input: String) = Uri.parse(input)
}

/**
 *  Adapter for [ZoneOffset] values.
 */
class ZoneOffsetAdapter {
    @ToJson
    fun toJson(value: ZoneOffset?): String? = value?.id

    // TODO Max supported range is +18:00 to -18:00 inclusive.
    @FromJson
    fun fromJson(input: String): ZoneOffset? = ZoneOffset.of(input)
}
