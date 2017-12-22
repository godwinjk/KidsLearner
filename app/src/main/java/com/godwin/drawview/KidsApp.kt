package com.godwin.drawview

import android.app.Application
import android.content.Context

/**
 * Created by Godwin on 02-12-2017 15:47 for DrawView.
 * @author : Godwin Joseph Kurinjikattu
 */
class KidsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
         var context: Context? = null
       public fun getAppContext(): Context {
            return context!!
        }
    }
}