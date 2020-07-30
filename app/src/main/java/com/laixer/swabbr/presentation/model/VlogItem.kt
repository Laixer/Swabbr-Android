package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.Vlog
import java.io.Serializable
import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID

val idList = listOf(
    1261, 1240, 1164, 1173, 2721, 1192, 1196, 1198, 1203, 1237, 1238, 1487, 1166,
    1170, 1178, 1181, 1183, 1184, 1185, 1186, 1187, 1188, 1191, 1194, 1197, 1165, 1200, 1168, 1468, 1548, 1541,
    1545, 1555, 1599, 1600, 1634, 1293, 1858, 1861, 1547, 2145, 2284, 2963, 2962, 2780, 3351, 3354, 2915, 3191,
    2923, 2285, 2308, 2286, 2306, 2307
)

data class VlogItem(
    val id: UUID,
    val userId: UUID,
    val isPrivate: Boolean,
    val dateStarted: ZonedDateTime,
    val views: Int
): Serializable {
    val url = with(idList.random()) {
        URL("https://assets.mixkit.co/videos/$this/$this-720.mp4")
    }
}

fun Vlog.mapToPresentation(): VlogItem =
    VlogItem(this.id, this.userId, this.isPrivate, this.dateStarted, this.views)

fun List<Vlog>.mapToPresentation(): List<VlogItem> = map { it.mapToPresentation() }
