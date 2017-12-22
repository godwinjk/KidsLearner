package com.godwin.drawview.ui.fragment.adapter

import android.view.View

/**
 * Created by Godwin on 28-11-2017 20:03 for DrawView.
 * @author : Godwin Joseph Kurinjikattu
 */
interface OnItemClickListener {
    fun onItemClicked(view: View?, item: Any?, position: Int?)
}