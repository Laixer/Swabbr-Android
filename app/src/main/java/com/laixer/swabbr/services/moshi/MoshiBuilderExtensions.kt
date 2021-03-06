package com.laixer.swabbr.services.moshi

import com.squareup.moshi.Moshi

/**
 *  Builds a Moshi object with custom adapters. These are required
 *  for proper json parsing throughout the application.
 */
fun Moshi.Builder.buildWithCustomAdapters(): Moshi = Moshi
    .Builder()
    .add(UuidJsonAdapter())
    .add(LocalDateAdapter())
    .add(ZonedDateTimeAdapter())
    .add(ZoneOffsetAdapter())
    .add(UriAdapter())
    .build()
