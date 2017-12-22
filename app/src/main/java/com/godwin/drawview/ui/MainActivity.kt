package com.godwin.drawview.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.widget.FrameLayout
import com.godwin.drawview.R
import com.godwin.drawview.ui.fragment.SplashFragment
import com.godwin.drawview.ui.view.ParallaxAnimationView

class MainActivity : BaseActivity() {
    private lateinit var mContainer: FrameLayout
    private lateinit var mFragmentManager: FragmentManager
    private lateinit var mAnimationView: ParallaxAnimationView

    private var mCurrentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mFragmentManager = supportFragmentManager
        initViews()

        attachInitially()
    }

    private fun initViews() {
        mContainer = findViewById(R.id.fl_container)
        mAnimationView = findViewById(R.id.pl_animation)

        mAnimationView.setBitmap(BitmapFactory.decodeResource(resources, R.drawable.cloud))
        mAnimationView.setSmallBitmap(BitmapFactory.decodeResource(resources, R.drawable.cloud_48))
//        mAnimationView.startAnimation()
    }

    private fun attachInitially() {
        attachFragment(SplashFragment.newInstance(), false)
    }

    public fun attachFragment(fragment: Fragment, isAddToBackStack: Boolean) {
        if (null == fragment) {
            return
        }
        if (0 < mFragmentManager.backStackEntryCount) {
            mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        val transaction = mFragmentManager.beginTransaction()
        transaction.replace(mContainer.id, fragment)

        if (isAddToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commitAllowingStateLoss()
        mCurrentFragment = fragment
    }
}
