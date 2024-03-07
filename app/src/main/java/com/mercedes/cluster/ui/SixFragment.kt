package com.mercedes.cluster.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mercedes.cluster.R

class SixFragment : Fragment() {

    companion object {
        fun newInstance() = SixFragment()
    }

    private lateinit var viewModel: SixViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_six, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SixViewModel::class.java)
        // TODO: Use the ViewModel
    }

}