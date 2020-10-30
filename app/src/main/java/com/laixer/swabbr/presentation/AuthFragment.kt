package com.laixer.swabbr.presentation

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.auth0.android.jwt.JWT
import com.laixer.presentation.Resource
import com.laixer.swabbr.injectFeature
import com.laixer.swabbr.presentation.auth.AuthUserViewModel
import com.laixer.swabbr.presentation.auth.AuthViewModel
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.getViewModel
import org.koin.core.qualifier.Qualifier
import java.util.*

/** Fragment that forces authentication on create, view creation and resume **/
abstract class AuthFragment : Fragment() {

    protected val authUserVm: AuthUserViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        injectFeature()
        super.onCreate(savedInstanceState)
    }


}


