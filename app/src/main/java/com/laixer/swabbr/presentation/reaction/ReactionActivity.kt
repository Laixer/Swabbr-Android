package com.laixer.swabbr.presentation.reaction

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.laixer.swabbr.R


class ReactionActivity : AppCompatActivity() {

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_container_reaction) as NavHostFragment }
    private val args: ReactionActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reaction)
        navHostFragment.navController.setGraph(R.navigation.nav_graph_reaction, ReactionActivityArgs(args.vlogId).toBundle())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        const val TAG = "ReactionActivity"
    }
}
