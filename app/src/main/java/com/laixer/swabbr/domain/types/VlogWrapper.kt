package com.laixer.swabbr.domain.types

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.VlogLikeSummary

/**
 *  Wrapper around a vlog, its vlog like summary and its user.
 */
data class VlogWrapper(
    val user: User,
    val vlog: Vlog,
    val vlogLikeSummary: VlogLikeSummary
)
