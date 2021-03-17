package com.laixer.swabbr.presentation.recording

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.laixer.swabbr.R
import kotlinx.android.synthetic.main.dev_fragment_empty.*

// TODO Remove
class DevEmptyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dev_fragment_empty, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dev_button_next.setOnClickListener { findNavController().navigate(DevEmptyFragmentDirections.actionDevEmptyFragmentToRecordingVideoWithPreviewFragment()) }
    }
}
