package com.mercedes.cluster.extention

import android.view.View
import android.widget.ImageView

fun ImageView.animateToInvisible() {
    animate().alpha(0f).setDuration(300).withEndAction { visibility = View.INVISIBLE }
}

fun ImageView.animateToVisible() {
    animate().alpha(1f).setDuration(300).withStartAction { visibility = View.VISIBLE }
}