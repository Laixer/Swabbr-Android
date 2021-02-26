package com.laixer.swabbr.utils

import android.net.Uri
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

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
 *  Adapter for [LocalDate] values.
 */
class LocalDateAdapter {
    @ToJson
    fun toJson(value: LocalDate?) = value?.format(DateTimeFormatter.ISO_LOCAL_DATE)

    @FromJson
    fun fromJson(input: String) =
        try {
            LocalDate.parse(input.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE)
        }
        catch (e: Exception) {
            throw e
        }
}

/**
 *  Adapter for [ZonedDateTime] values.
 */
class ZonedDateTimeAdapter {
    @ToJson
    fun toJson(value: ZonedDateTime?) = value?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    @FromJson
    fun fromJson(input: String) =
        try {
            ZonedDateTime.parse(input, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }
        catch (e: Exception) {
            throw e
        }
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
