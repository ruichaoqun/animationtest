package com.ts.clusterapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.DisplayManager.DisplayListener
import android.hardware.display.VirtualDisplay
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ts.clusterapp.R
import com.ts.clusterapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "Cluster.MainActivity"

    private val displayName = "MapVirtualDisplay"
    private var isOpen = false
    private lateinit var mBinding: ActivityMainBinding

    private lateinit var leftOpenAnimatorSet: AnimatorSet
    private lateinit var leftCloseAnimatorSet: AnimatorSet
    private lateinit var rightOpenAnimatorSet: AnimatorSet
    private lateinit var rightCloseAnimatorSet: AnimatorSet

    private var mFragments: MutableList<Fragment> = mutableListOf()
    private var mCurrentPosition = -1

    private var translationX: Float = 0f
    private var translationY: Float = 0f
    private var rotationY: Float = 45f

    private var mVirtualDisplay: VirtualDisplay? = null
    private lateinit var mDisplayManager: DisplayManager
    private var mRegisteredNavDisplayId: Int = Display.INVALID_DISPLAY


    private val mDisplayListener: DisplayListener = object : DisplayListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onDisplayAdded(displayId: Int) {
            val navDisplayId: Int = getVirtualDisplayId()
            Log.i(
                TAG, "onDisplayAdded, displayId: " + displayId
                        + ", navigation display id: " + navDisplayId
            )
            if (navDisplayId == displayId) {
                mRegisteredNavDisplayId = displayId
                launchNavigationActivity()
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onDisplayRemoved(displayId: Int) {
            if (mRegisteredNavDisplayId === displayId) {
                mRegisteredNavDisplayId = Display.INVALID_DISPLAY
                launchNavigationActivity()
            }
        }

        override fun onDisplayChanged(displayId: Int) {}
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        translationX = resources.getDimensionPixelSize(R.dimen.dp_300).toFloat()
        translationY = resources.getDimensionPixelSize(R.dimen.dp_250).toFloat()

        initAnimator()
        initFragments()
        initMap()
        mBinding.button.setOnClickListener {
            switchFragment()
        }
    }

    @SuppressLint("CommitTransaction")
    private fun initFragments() {
        mFragments.add(FirstFragment())
        mFragments.add(SecondFragment())
        mFragments.add(ThirdFragment())
        val transaction = supportFragmentManager.beginTransaction()
        for (fragment in mFragments) {
            transaction.add(R.id.frame_layout, fragment)
        }
        transaction.commit()
        switchFragment()
    }

    private fun hideAllFragment(transaction: FragmentTransaction) {
        for (fragment in mFragments) {
            transaction.hide(fragment)
        }
    }

    private fun switchFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in,R.anim.fade_out)
        hideAllFragment(transaction)
        if (mCurrentPosition >= mFragments.size - 1) {
            mCurrentPosition = 0
        } else{
            mCurrentPosition++
        }
        transaction.show(mFragments[mCurrentPosition]).commit()
        Log.i(TAG,"switchFragment  mCurrentPosition:$mCurrentPosition")
    }

    private fun initMap() {
        mDisplayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        mDisplayManager.registerDisplayListener(mDisplayListener, Handler(Looper.myLooper()!!))
        mBinding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.i(TAG, "surfaceCreated,holder: $holder")
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int,
            ) {
                Log.i(
                    TAG,
                    "surfaceChanged, holder: $holder , size: $width x $height , format:$format"
                )
//                if (mVirtualDisplay == null) {
//                    mVirtualDisplay = createVirtualDisplay(holder.surface, width, height)
//                } else {
//                    mVirtualDisplay?.surface = holder.surface
//                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.i(
                    TAG,
                    "surfaceDestroyed, holder: $holder , detaching surface from display, surface: ${holder.surface}"
                )
                mVirtualDisplay?.surface = null
            }
        })
    }

    private fun createVirtualDisplay(surface: Surface, width: Int, height: Int): VirtualDisplay {
        Log.i(TAG, "createVirtualDisplay, surface: $surface , width: ${width}x${height}")
        return mDisplayManager.createVirtualDisplay(
            displayName, width, height, resources.displayMetrics.densityDpi,
            surface, DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
        )
    }

    private fun getVirtualDisplayId(): Int {
        return mVirtualDisplay?.display?.displayId ?: Display.INVALID_DISPLAY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun launchNavigationActivity() {
        if (mRegisteredNavDisplayId === Display.INVALID_DISPLAY) {
            Log.i(TAG, String.format("Launch activity ignored (no display yet)"))
            return
        }
        val navigationActivity = ComponentName(
            getString(R.string.navigation_application_id),
            getString(R.string.navigation_className)
        )
        try {
            val intent: Intent = Intent(Intent.ACTION_MAIN)
                .setComponent(navigationActivity)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Log.i(TAG, "Launching: $intent on display: $mRegisteredNavDisplayId")
            val activityOptions = ActivityOptions.makeBasic()
                .setLaunchDisplayId(mRegisteredNavDisplayId)
                .toBundle()
            startActivity(intent, activityOptions)
        } catch (ex: Exception) {
            Log.e(TAG, "Unable to start navigation activity: $navigationActivity", ex)
        }
    }

    private fun initAnimator() {
        leftOpenAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    mBinding.layoutDashBoardSpeed,
                    View.ROTATION_Y,
                    0f,
                    rotationY
                ).setDuration(1000),
                ObjectAnimator.ofFloat(
                    mBinding.layoutDashBoardSpeed,
                    View.TRANSLATION_X,
                    0f,
                    -translationX
                )
                    .setDuration(2000),
                ObjectAnimator.ofFloat(
                    mBinding.layoutDashBoardSpeed,
                    View.TRANSLATION_Y,
                    0f,
                    translationY
                ).setDuration(2000),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardSpeed, View.ALPHA, 1.0f, 0f)
                    .setDuration(1200)
            )
        }

        leftCloseAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    mBinding.layoutDashBoardSpeed,
                    View.ROTATION_Y,
                    rotationY,
                    0f
                ).setDuration(500),
                ObjectAnimator.ofFloat(
                    mBinding.layoutDashBoardSpeed,
                    View.TRANSLATION_X,
                    -translationX,
                    0f
                )
                    .setDuration(500),
                ObjectAnimator.ofFloat(
                    mBinding.layoutDashBoardSpeed,
                    View.TRANSLATION_Y,
                    translationY,
                    0f
                ).setDuration(500),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardSpeed, View.ALPHA, 0f, 1f)
            )
            duration = 1000
        }

        rightOpenAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    mBinding.layoutDashBoardRotating,
                    View.ROTATION_Y,
                    0f,
                    -rotationY
                ).setDuration(500),
                ObjectAnimator.ofFloat(
                    mBinding.layoutDashBoardRotating,
                    View.TRANSLATION_X,
                    0f,
                    translationX
                )
                    .setDuration(500),
                ObjectAnimator.ofFloat(
                    mBinding.layoutDashBoardRotating,
                    View.TRANSLATION_Y,
                    0f,
                    translationY
                )
                    .setDuration(500),
                ObjectAnimator.ofFloat(mBinding.layoutDashBoardRotating, View.ALPHA, 1.0f, 0f)
                    .apply {
                        interpolator = AccelerateInterpolator()
                    }
            )
            duration = 1000
        }

        rightCloseAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    mBinding.layoutDashBoardRotating,
                    View.ROTATION_Y,
                    -rotationY,
                    0f
                ).setDuration(500),
                ObjectAnimator.ofFloat(
                    mBinding.layoutDashBoardRotating,
                    View.TRANSLATION_X,
                    translationX,
                    0f
                )
                    .setDuration(500),
                ObjectAnimator.ofFloat(
                    mBinding.layoutDashBoardRotating,
                    View.TRANSLATION_Y,
                    translationY,
                    0f
                )
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