package com.laixer.swabbr.presentation.streaming

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.laixer.swabbr.R


class StreamActivity : AppCompatActivity() {

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_container_stream) as NavHostFragment }
    private val args: StreamActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_stream)
        navHostFragment.navController.setGraph(R.navigation.nav_graph_stream, StreamFragmentArgs(args.livestreamId).toBundle())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        const val TAG = "LivestreamActivity"
    }
}
