package com.laixer.swabbr.presentation.vlogs.recording

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.laixer.swabbr.R

/**
 *  Container in which the fragments for recording a vlog exist.
 */
class RecordVlogActivity : AppCompatActivity() {
    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_container_stream) as NavHostFragment }
    private val args: RecordVlogActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_record_vlog)
        navHostFragment.navController.setGraph(
            R.navigation.nav_graph_recordvlog,
            RecordVlogActivityArgs(args.vlogId).toBundle() // TODO Should be fragment args
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        const val TAG = "RecordVlogActivity"
    }
}
