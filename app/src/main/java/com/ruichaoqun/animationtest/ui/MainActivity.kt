package com.ruichaoqun.animationtest.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ruichaoqun.animationtest.R
import com.ruichaoqun.animationtest.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    var isOpen = false
    lateinit var mBinding: ActivityMainBinding

    lateinit var leftOpenAnimatorSet: AnimatorSet
    lateinit var leftCloseAnimatorSet: AnimatorSet
    lateinit var rightOpenAnimatorSet: AnimatorSet
    lateinit var rightCloseAnimatorSet: AnimatorSet

    var translationX: Float = 0f
    var translationY: Float = 0f
    var rotationY: Float = 45f

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.button.setOnClickListener {
            createDisplay(surface)
//            if (isOpen) {
//                close()
//            } else {
//                open()
//            }
            val position = (mBinding.viewPager.currentItem + 1)% 6
            mBinding.viewPager.setCurrentItem(position,false)
        }
        translationX = resources.getDimensionPixelSize(R.dimen.dp_300).toFloat()
        translationY = resources.getDimensionPixelSize(R.dimen.dp_250).toFloat()
        initAnimator()
        mBinding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        mBinding.viewPager.adapter = CommonPagerAdapter(this,6) {
            provideFragment(it)
        }
        mBinding.viewPager.offscreenPageLimit = 6

        initMap()
    }

    lateinit var surface:Surface
    private fun initMap() {
        mBinding.surfaceView.holder.addCallback(object:SurfaceHolder.Callback{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun surfaceCreated(holder: SurfaceHolder) {
                surface = holder.surface
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDisplay(surface: Surface) {
        val displayName = "MapVirtualDisplay"
        val metrics = resources.displayMetrics
        val displayManager: DisplayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val virtualDisplay:VirtualDisplay = displayManager.createVirtualDisplay(displayName,
            1920,720,metrics.densityDpi,surface,DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY)
        virtualDisplay.display?.apply {
            Log.e("AAAAA","display success create")
            val packageName = "com.ruichaoqun.myapplication"
            val intent = Intent()
            intent.component = ComponentName(packageName,"com.ruichaoqun.myapplication.MainActivity")
            intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or Intent.FLAG_ACTIVITY_NEW_TASK)
            val options = ActivityOptions.makeBasic().setLaunchDisplayId(displayId)
            Log.e("AAAAA","displayId:$displayId")
            startActivity(intent,options.toBundle())
        }
    }

    private fun provideFragment(position:Int) :Fragment{
        return when(position) {
            0 -> FirstFragment()
            1 -> SecondFragment()
            2 -> SecondFragment()
            3 -> SecondFragment()
            4 -> FirstFragment()
            5 -> FirstFragment()
            else -> FirstFragment()
        }
    }


    private fun initAnimator() {
        leftOpenAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardSpeed, View.ROTATION_Y, 0f, rotationY).setDuration(1000),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardSpeed, View.TRANSLATION_X, 0f, -translationX)
                    .setDuration(2000),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardSpeed, View.TRANSLATION_Y, 0f, translationY).setDuration(2000),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardSpeed, View.ALPHA, 1.0f, 0f).setDuration(1200)
            )
        }

        leftCloseAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardSpeed, View.ROTATION_Y, rotationY, 0f).setDuration(500),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardSpeed, View.TRANSLATION_X, -translationX, 0f)
                    .setDuration(500),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardSpeed, View.TRANSLATION_Y, translationY, 0f).setDuration(500),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardSpeed, View.ALPHA, 0f, 1f)
            )
            duration = 1000
        }

        rightOpenAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardRotating, View.ROTATION_Y, 0f, -rotationY).setDuration(500),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardRotating, View.TRANSLATION_X, 0f, translationX)
                    .setDuration(500),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardRotating, View.TRANSLATION_Y, 0f, translationY)
                    .setDuration(500),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardRotating, View.ALPHA, 1.0f, 0f).apply {
                    interpolator = AccelerateInterpolator()
                }
            )
            duration = 1000
        }

        rightCloseAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardRotating, View.ROTATION_Y, -rotationY, 0f).setDuration(500),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardRotating, View.TRANSLATION_X, translationX, 0f)
                    .setDuration(500),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardRotating, View.TRANSLATION_Y, translationY, 0f)
                    .setDuration(500),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardRotating, View.ALPHA, 0f, 1f)
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