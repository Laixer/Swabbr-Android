package com.laixer.swabbr.presentation.livestream

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.laixer.swabbr.R


class LivestreamActivity : AppCompatActivity() {

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_container_livestream) as NavHostFragment }
    private val args: LivestreamActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_livestream)
        navHostFragment.navController.setGraph(R.navigation.nav_graph_livestream, LivestreamFragmentArgs(args.livestreamId).toBundle())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        const val TAG = "LivestreamActivity"
    }
}
