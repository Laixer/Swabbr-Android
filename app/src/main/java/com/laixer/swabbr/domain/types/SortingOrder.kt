package com.laixer.swabbr.domain.types

// TODO This doesn't translate to the actual enum.
/**
 *  Enum representing the sorting order of some data set.
 */
enum class SortingOrder(val value: Int) {
    UNSORTED(0),
    ASCENDING(1),
    DESCENDING(2),
}
