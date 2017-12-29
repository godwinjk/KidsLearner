package com.godwin.drawview.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.godwin.drawview.support.FileManager

/**
 * Created by Godwin on 25-11-2017 15:50 for DrawView.
 * @author : Godwin Joseph Kurinjikattu
 */
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FileManager.isPermissionGranted(this)) {
//need to complete
        } else {
            FileManager.showPermissionDialog(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            // permission was granted, yay! Do the
            // contacts-related task you need to do.

        } else {
            FileManager.showPermissionDialog(this)
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
        }
        return;
    }
}