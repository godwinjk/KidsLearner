package com.godwin.drawview.ui.fragment.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.godwin.drawview.R
import com.godwin.drawview.model.HomeItem

/**
 * Created by Godwin on 28-11-2017 17:59 for DrawView.
 * @author : Godwin Joseph Kurinjikattu
 */

class HomeItemAdapter : RecyclerView.Adapter<HomeItemAdapter.ViewHolder>() {
    private lateinit var mHomeItems: List<HomeItem>
    private lateinit var mListener: OnItemClickListener

    override fun getItemCount(): Int {
        return if (mHomeItems == null) 0 else mHomeItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_home_item, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val homeItem = mHomeItems[position]
        holder.icon.setImageResource(homeItem.iconId)
        holder.icon.setOnClickListener(View.OnClickListener {
            if (null != mListener) {
                mListener.onItemClicked(holder.icon, homeItem, position)
            }
        })
        holder.icon.setOnLongClickListener({ view ->
            if (null != mListener)
                mListener.onItemLongPressed(holder.icon, homeItem, position)
            false
        })
    }

    fun setHomeItems(mHomeItems: List<HomeItem>) {
        this.mHomeItems = mHomeItems
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icon: ImageView = view.findViewById(R.id.iv_item)
    }
}