package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration

interface AuthRepository {

    fun login(login: Login)

    fun register(registration: Registration)
}
