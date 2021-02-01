package com.laixer.swabbr.domain.types

/**
 *  Used to control segmentation and sorting of result sets.
 */
data class Pagination (
    val sortingOrder: SortingOrder,
    val limit: Int,
    val offset: Int)
{
    companion object {
        /**
         *  Generates a default navigation.
         */
        fun default(): Pagination = Pagination (
            sortingOrder = SortingOrder.UNSORTED,
            limit = 25,
            offset = 0)

        /**
         *  Generates a navigation which gets us the latest items.
         */
        fun latest(): Pagination = Pagination (
            sortingOrder = SortingOrder.DESCENDING,
            limit = 25,
            offset = 0)
    }
}
