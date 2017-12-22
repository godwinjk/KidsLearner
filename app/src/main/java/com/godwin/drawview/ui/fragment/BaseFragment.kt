package com.godwin.drawview.ui.fragment

import android.content.Context
import android.support.v4.app.Fragment
import com.godwin.drawview.model.HomeItem
import com.godwin.drawview.ui.MainActivity

/**
 * Created by Godwin on 28-11-2017 11:40 for DrawView.
 * @author : Godwin Joseph Kurinjikattu
 */
abstract class BaseFragment : Fragment() {
    protected lateinit var mActivity: MainActivity
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as MainActivity
    }

    fun isLastPosition(item: HomeItem, currentPosition: Int): Boolean {
        when (item.id.toInt()) {
            1 -> {
                return currentPosition >= 9
            }
            2 -> {
                return currentPosition >= 25
            }
            3 -> {
                return currentPosition >= 25
            }
        }
        return false
    }

    fun getMaxPosition(item: HomeItem): Int {
        when (item.id.toInt()) {
            1 -> {
                return 9
            }
            2 -> {
                return 25
            }
            3 -> {
                return 25
            }
        }
        return 0
    }
}