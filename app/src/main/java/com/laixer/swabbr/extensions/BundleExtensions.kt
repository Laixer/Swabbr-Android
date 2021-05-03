package com.laixer.swabbr.extensions

import android.os.Bundle
import java.util.*

/**
 *  Add a uuid to a bundle.
 */
fun Bundle.putUuid(key: String, value: UUID) = this.putString(key, value.toString())

/**
 *  Get a uuid from a bundle.
 */
fun Bundle.getUuid(key: String): UUID = UUID.fromString(getString(key))

/**
 *  Gets a uuid from a bundle or null if [containsKey] returns false.
 */
fun Bundle.getUuidOrNull(key: String): UUID? =
    if (!containsKey(key)) {
        null
    } else {
        getUuid(key)
    }
