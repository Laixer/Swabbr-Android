package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single

interface AuthRepository {

    fun login(login: Login): Single<Pair<Pair<String, User>, Settings>>

    fun register(registration: Registration): Single<Pair<Pair<String, User>, Settings>>

    fun getToken(): Single<String>
}
