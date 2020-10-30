package com.laixer.swabbr.presentation.model

import android.net.Uri
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.VlogData
import com.laixer.swabbr.domain.model.VlogLikeSummary

data class VlogItem(
    val data: VlogData,
    val vlogLikeSummary: VlogLikeSummary,
    val thumbnailUri: Uri?
) {
    fun equals(compare: VlogItem): Boolean =
        this.data.equals(compare.data) && this.vlogLikeSummary.equals(compare.vlogLikeSummary)
}


fun Vlog.mapToPresentation(): VlogItem =
    VlogItem(this.data, this.vlogLikeSummary, this.thumbnailUri)

fun List<Vlog>.mapToPresentation(): List<VlogItem> = map { it.mapToPresentation() }
