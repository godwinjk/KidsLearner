package com.godwin.drawview.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godwin.drawview.R
import java.util.*


class SplashFragment : BaseFragment() {
    private var mTimer: Timer? = null


    companion object {
        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTimer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                navigateToHome()
            }
        }
        mTimer!!.schedule(task, 3 * 1000)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (null != mTimer) {
            mTimer!!.cancel()
            mTimer = null
        }
    }

    private fun navigateToHome() {
        mActivity.runOnUiThread({
            mActivity.attachFragment(HomeFragment.newInstance(), false)
        })
    }
}// Required empty public constructor
