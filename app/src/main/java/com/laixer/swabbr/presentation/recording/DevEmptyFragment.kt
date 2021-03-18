package com.laixer.swabbr.presentation.recording

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import com.laixer.swabbr.R
import kotlinx.android.synthetic.main.dev_fragment_empty.*

// TODO Remove
class DevEmptyFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askPermission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO) { /* Do nothing */ }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dev_fragment_empty, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dev_button_next.setOnClickListener { findNavController().navigate(DevEmptyFragmentDirections.actionDevEmptyFragmentToRecordingVideoWithPreviewFragment()) }
    }
}
