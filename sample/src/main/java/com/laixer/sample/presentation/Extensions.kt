package com.laixer.sample.presentation

import android.widget.ImageView
import com.laixer.presentation.loadImageRound

fun ImageView.loadAvatar(id: String) =
    loadImageRound("https://api.adorable.io/avatars/285/$id")
