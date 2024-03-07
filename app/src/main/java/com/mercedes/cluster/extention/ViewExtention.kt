package com.mercedes.cluster.extention

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

const val TRANSLATION_X: Float = 680f
const val TRANSLATION_Y: Float = 680f
const val ROTATION_Y: Float = 45f

fun View.animateToLeftBottom(isRevert: Boolean) {
    if (isRevert) {
        AnimatorSet().let {
            it.playTogether(
                ObjectAnimator.ofFloat(this, View.ROTATION_Y, ROTATION_Y, 0f).setDuration(1000),
                ObjectAnimator.ofFloat(this, View.TRANSLATION_X, -TRANSLATION_X, 0f).setDuration(2000),
                ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, TRANSLATION_Y, 0f).setDuration(2000),
                ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f).setDuration(1200)
            )
            it.start()
        }
    }else {
        AnimatorSet().let {
            it.playTogether(
                ObjectAnimator.ofFloat(this, View.ROTATION_Y, 0f, ROTATION_Y).setDuration(1000),
                ObjectAnimator.ofFloat(this, View.TRANSLATION_X, 0f, -TRANSLATION_X).setDuration(2000),
                ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 0f, TRANSLATION_Y).setDuration(2000),
                ObjectAnimator.ofFloat(this, View.ALPHA, 1.0f, 0f).setDuration(1200)
            )
            it.start()
        }
    }
}

fun View.animateToRightBottom(isRevert: Boolean) {
    if (isRevert) {
        AnimatorSet().let {
            it.playTogether(
                ObjectAnimator.ofFloat(this, View.ROTATION_Y, -ROTATION_Y, 0f).setDuration(1000),
                ObjectAnimator.ofFloat(this, View.TRANSLATION_X, TRANSLATION_X, 0f).setDuration(2000),
                ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, TRANSLATION_Y, 0f).setDuration(2000),
                ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f).setDuration(1200)
            )
            it.start()
        }
    }else {
        AnimatorSet().let {
            it.playTogether(
                ObjectAnimator.ofFloat(this, View.ROTATION_Y, 0f, -ROTATION_Y).setDuration(1000),
                ObjectAnimator.ofFloat(this, View.TRANSLATION_X, 0f, TRANSLATION_X).setDuration(2000),
                ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 0f, TRANSLATION_Y).setDuration(2000),
                ObjectAnimator.ofFloat(this, View.ALPHA, 1.0f, 0f).setDuration(1200)
            )
            it.start()
        }
    }
}