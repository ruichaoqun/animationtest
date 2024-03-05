package com.ruichaoqun.animationtest.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class CommonPagerAdapter(fragmentActivity: FragmentActivity,
                         private val count:Int, val fragment:(position:Int)->Fragment): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = count

    override fun createFragment(position: Int): Fragment = fragment(position)
}