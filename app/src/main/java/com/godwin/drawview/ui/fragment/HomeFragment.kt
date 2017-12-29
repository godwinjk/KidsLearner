package com.godwin.drawview.ui.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godwin.drawview.R
import com.godwin.drawview.model.HomeItem
import com.godwin.drawview.ui.common.AudioManager
import com.godwin.drawview.ui.fragment.adapter.HomeItemAdapter
import com.godwin.drawview.ui.fragment.adapter.OnItemClickListener


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : BaseFragment(), OnItemClickListener {

    private lateinit var rvMain: RecyclerView
    private lateinit var mAdapter: HomeItemAdapter

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvMain = view.findViewById(R.id.rvMain)
        rvMain.layoutManager = GridLayoutManager(mActivity, 2)
        rvMain.setHasFixedSize(true)

        mAdapter = HomeItemAdapter()
        rvMain.adapter = mAdapter

        mAdapter.setHomeItems(getHomeIcons())
        mAdapter.setOnItemClickListener(this)
    }

    private fun getHomeIcons(): List<HomeItem> {
        return listOf(HomeItem("", 1, R.drawable.numbers, null),
                HomeItem("", 2, R.drawable.alpha_l, null),
                HomeItem("", 3, R.drawable.alpha_u, null),
                HomeItem("", 4, R.drawable.words, null))
    }

    override fun onItemClicked(view: View?, item: Any?, position: Int?) {
        AudioManager.playButtonClick()
        val homeItem = item as HomeItem
        when (homeItem.iconId) {
            R.drawable.numbers -> {
                mActivity.attachFragment(WriteFragment.newInstance(homeItem,false), true)
            }
            R.drawable.alpha_l -> {
                mActivity.attachFragment(WriteFragment.newInstance(homeItem,false), true)
            }
            R.drawable.alpha_u -> {
                mActivity.attachFragment(WriteFragment.newInstance(homeItem,false), true)
            }
            R.drawable.words -> {
                mActivity.attachFragment(WriteFragment.newInstance(homeItem,false), true)
            }
        }
    }

    override fun onItemLongPressed(view: View?, item: Any?, position: Int?) {
        val homeItem = item as HomeItem
        when (homeItem.iconId) {
            R.drawable.numbers -> {
                mActivity.attachFragment(WriteFragment.newInstance(homeItem,true), true)
            }
            R.drawable.alpha_l -> {
                mActivity.attachFragment(WriteFragment.newInstance(homeItem,true), true)
            }
            R.drawable.alpha_u -> {
                mActivity.attachFragment(WriteFragment.newInstance(homeItem,true), true)
            }
            R.drawable.words -> {
                mActivity.attachFragment(WriteFragment.newInstance(homeItem,true), true)
            }
        }
    }

}// Required empty public constructor
