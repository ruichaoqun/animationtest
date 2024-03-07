package com.mercedes.cluster.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.mercedes.cluster.R
import com.mercedes.cluster.databinding.ActivityMainBinding
import com.mercedes.cluster.extention.animateToInvisible
import com.mercedes.cluster.extention.animateToLeftBottom
import com.mercedes.cluster.extention.animateToRightBottom
import com.mercedes.cluster.extention.animateToVisible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "Cluster.MainActivity"
    private val displayName = "MapVirtualDisplay"

    private lateinit var mBinding: ActivityMainBinding
    private var mFragments: MutableList<Fragment> = mutableListOf()
    private var mCurrentPosition = -1

    private var mVirtualDisplay: VirtualDisplay? = null
    private lateinit var mDisplayManager: DisplayManager
    private var mRegisteredNavDisplayId: Int = Display.INVALID_DISPLAY
    private var isFullScreenTheme = false
    private var isFunctionTheme = false


    private val mDisplayListener: DisplayListener = object : DisplayListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onDisplayAdded(displayId: Int) {
            val navDisplayId: Int = getVirtualDisplayId()
            Log.i(
                TAG, "onDisplayAdded, displayId: " + displayId
                        + ", navigation display id: " + navDisplayId
            )
//            if (navDisplayId == displayId) {
//                mRegisteredNavDisplayId = displayId
//                launchNavigationActivity()
//            }
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

        initFragments()
        initMap()

        var receiver = TestReceiver()
        registerReceiver(receiver, IntentFilter("com.mercedes.cluster.ui"))
    }

    @SuppressLint("CommitTransaction")
    private fun initFragments() {
        mFragments.add(FirstFragment())
        mFragments.add(SecondFragment())
        mBinding.indicatorView.setCount(mFragments.size)
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
        transaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
        hideAllFragment(transaction)
        if (mCurrentPosition >= mFragments.size - 1) {
            mCurrentPosition = 0
        } else{
            mCurrentPosition++
        }
        transaction.show(mFragments[mCurrentPosition]).commit()
        Log.i(TAG,"switchFragment  mCurrentPosition:$mCurrentPosition")
        mBinding.indicatorView.setCurrentPosition(mCurrentPosition)
    }

    private fun initMap() {
        mDisplayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        mDisplayManager.registerDisplayListener(mDisplayListener, Handler(Looper.myLooper()!!))
        mBinding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.i(TAG, "surfaceCreated,holder: $holder")
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int, ) {
                Log.i(TAG, "surfaceChanged, holder: $holder , size: $width x $height , format:$format")
                if (mVirtualDisplay == null) {
                    mVirtualDisplay = createVirtualDisplay(holder.surface, width, height)
                } else {
                    mVirtualDisplay?.surface = holder.surface
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.i(TAG, "surfaceDestroyed, holder: $holder , detaching surface from display, surface: ${holder.surface}")
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

    private fun switchToNormal() {
        if (isFullScreenTheme) {
            isFullScreenTheme = false
            showDashBoard()
            mBinding.ivBg.animateToInvisible()
            mBinding.ivBgMask.setImageResource(R.drawable.bg_mask_2)
            mBinding.ivBgMask.animateToVisible()
            isFunctionTheme = false
        } else {
            if (isFunctionTheme) {
                switchFragment()
                mBinding.ivBg.animateToInvisible()
                mBinding.ivBgMask.setImageResource(R.drawable.bg_mask_2)
                mBinding.ivBgMask.animateToVisible()
                isFunctionTheme = false
            } else {
                switchToFunctionPage()
            }
        }
    }

    private fun switchToFullMap() {
        if (isFullScreenTheme) {
            if (isFunctionTheme) {
                switchToFunctionPage()
            } else {
                switchToNormal()
            }
        } else {
            if (isFunctionTheme) {
                switchFragment()
                isFunctionTheme = false
            }
            isFullScreenTheme = true
            hideDashBoard()
            mBinding.ivBg.animateToInvisible()
            mBinding.ivBgMask.setImageResource(R.drawable.bg_mask)
            mBinding.ivBgMask.animateToVisible()
        }
    }

    private fun switchToFunctionPage() {
        if (isFullScreenTheme) {
            showDashBoard()
            switchFragment()
            mBinding.ivBgMask.animateToInvisible()
            mBinding.ivBg.animateToVisible()
            isFullScreenTheme = false
            isFunctionTheme = true
        } else {
            if (isFunctionTheme) {
                switchToNormal()
            } else {
                isFunctionTheme = true
                switchFragment()
                mBinding.ivBgMask.animateToInvisible()
                mBinding.ivBg.animateToVisible()
            }
        }
    }

    private fun showDashBoard() {
        mBinding.layoutDashBoardSpeed.animateToLeftBottom(true)
        mBinding.layoutDashBoardRotating.animateToRightBottom(true)
    }

    private fun hideDashBoard() {
        mBinding.layoutDashBoardSpeed.animateToLeftBottom(false)
        mBinding.layoutDashBoardRotating.animateToRightBottom(false)
    }

    inner class TestReceiver : BroadcastReceiver() {
        var lastTime = System.currentTimeMillis()
        override fun onReceive(context: Context?, intent: Intent?) {
            var code = intent?.getIntExtra("mode",0)
            if (System.currentTimeMillis() - lastTime < 5000) {
                return
            }
            lastTime = System.currentTimeMillis()
            when (code) {
                0 -> {
                    switchToNormal()
                }
                1 -> {
                    switchToFunctionPage()
                }
                2 -> {
                    switchToFullMap()
                }
                else -> {
                    switchToNormal()
                }
            }
        }

    }
}