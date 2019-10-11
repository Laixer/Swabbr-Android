package com.laixer.sample.presentation.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.laixer.navigation.features.SampleNavigation
import com.laixer.sample.R
import com.laixer.sample.injectFeature
import org.koin.androidx.viewmodel.ext.viewModel

class ProfileActivity : AppCompatActivity() {

    private val vm: ProfileViewModel by viewModel()
    private val userId by lazy { intent.getStringExtra(SampleNavigation.USER_ID_KEY) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        injectFeature()

        if (savedInstanceState == null) {
            vm.getProfile(userId)
        }
    }
}