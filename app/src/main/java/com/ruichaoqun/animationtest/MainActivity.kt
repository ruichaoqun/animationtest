package com.ruichaoqun.animationtest

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    lateinit var view: View
    lateinit var view2: View
    var isOpen = false

    lateinit var leftOpenAnimatorSet: AnimatorSet
    lateinit var leftCloseAnimatorSet: AnimatorSet
    lateinit var rightOpenAnimatorSet: AnimatorSet
    lateinit var rightCloseAnimatorSet: AnimatorSet

    var translationX: Float = 0f
    var translationY: Float = 0f
    var rotationY: Float = 45f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_main)
        view = findViewById(R.id.layout_1)
        view2 = findViewById(R.id.layout_2)

        findViewById<View>(R.id.button).setOnClickListener {
            if (isOpen) {
                close()
            } else {
                open()
            }
        }
        translationX = resources.getDimensionPixelSize(R.dimen.dp_300).toFloat()
        translationY = resources.getDimensionPixelSize(R.dimen.dp_250).toFloat()
        initAnimator()
    }


    private fun initAnimator() {
        leftOpenAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, View.ROTATION_Y, 0f, rotationY).setDuration(1000),
                ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, -translationX)
                    .setDuration(2000),
                ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, translationY).setDuration(2000),
                ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 0f).setDuration(1200)
            )
        }

        leftCloseAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, View.ROTATION_Y, rotationY, 0f).setDuration(500),
                ObjectAnimator.ofFloat(view, View.TRANSLATION_X, -translationX, 0f)
                    .setDuration(500),
                ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, translationY, 0f).setDuration(500),
                ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
            )
            duration = 1000
        }

        rightOpenAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view2, View.ROTATION_Y, 0f, -rotationY).setDuration(500),
                ObjectAnimator.ofFloat(view2, View.TRANSLATION_X, 0f, translationX)
                    .setDuration(500),
                ObjectAnimator.ofFloat(view2, View.TRANSLATION_Y, 0f, translationY)
                    .setDuration(500),
                ObjectAnimator.ofFloat(view2, View.ALPHA, 1.0f, 0f).apply {
                    interpolator = AccelerateInterpolator()
                }
            )
            duration = 1000
        }

        rightCloseAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view2, View.ROTATION_Y, -rotationY, 0f).setDuration(500),
                ObjectAnimator.ofFloat(view2, View.TRANSLATION_X, translationX, 0f)
                    .setDuration(500),
                ObjectAnimator.ofFloat(view2, View.TRANSLATION_Y, translationY, 0f)
                    .setDuration(500),
                ObjectAnimator.ofFloat(view2, View.ALPHA, 0f, 1f)
            )

            duration = 1000
        }
    }

    private fun open() {
        leftOpenAnimatorSet.start()
        rightOpenAnimatorSet.start()
        isOpen = true
    }

    private fun close() {
        leftCloseAnimatorSet.start()
        rightCloseAnimatorSet.start()
        isOpen = false
    }
}