package com.laixer.swabbr.domain.exceptions

import java.lang.Exception

/**
 *  Base class for Swabbr extensions.
 */
abstract class SwabbrBaseException(message: String?) : Exception(message)
