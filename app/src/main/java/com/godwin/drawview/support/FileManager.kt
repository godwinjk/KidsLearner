package com.godwin.drawview.support

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.godwin.drawview.KidsApp
import com.godwin.drawview.model.HomeItem
import com.godwin.handdrawview.TimePoint
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.Permission
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Godwin on 02-12-2017 15:42 for DrawView.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

class FileManager {
    companion object {
        fun getBitmap(item: HomeItem, position: Int): Bitmap {
            val manager = KidsApp.getAppContext().assets
            val name = StringBuilder()
            when (item.id.toInt()) {
                1 -> {
                    name.append("n_")
                }
                2 -> {
                    name.append("al_l_")
                }
                3 -> {
                    name.append("al_u_")
                }
            }
            name.append(position)
            val ips = manager.open(name.toString())
            return BitmapFactory.decodeStream(ips)
        }

        fun getBitmapFromExtern(item: HomeItem, position: Int): Bitmap? {
            val dir = File(Environment.getExternalStorageDirectory(), "KidsApp")
            val name = StringBuilder()
            when (item.id.toInt()) {
                1 -> {
                    name.append("n_")
                }
                2 -> {
                    name.append("al_l_")
                }
                3 -> {
                    name.append("al_u_")
                }
            }
            name.append(position)
//            name.append(".png")
//            val file = File(dir, name.toString())
//            if (!file.exists())
//                return null
            val id = KidsApp.context?.resources?.getIdentifier(name.toString(), "raw", KidsApp.context?.packageName)
            return BitmapFactory.decodeResource(KidsApp.context?.resources, id!!)
        }

        fun writeBitmapToExtern(item: HomeItem, position: Int, bitmap: Bitmap): Bitmap {
            val dir = File(Environment.getExternalStorageDirectory(), "KidsApp")
            if (!dir.exists())
                dir.mkdirs()

            val name = StringBuilder()
            when (item.id.toInt()) {
                1 -> {
                    name.append("n_")
                }
                2 -> {
                    name.append("al_l_")
                }
                3 -> {
                    name.append("al_u_")
                }
            }
            name.append(position)
            name.append(".png")
            val file = File(dir, name.toString())
            val os = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            return BitmapFactory.decodeFile(file.absolutePath)
        }

        fun writeTextToExtern(item: HomeItem, position: Int, timePoints: List<TimePoint>) {
            val dir = File(Environment.getExternalStorageDirectory(), "KidsApp")
            if (!dir.exists())
                dir.mkdirs()
            val name = StringBuilder()
            when (item.id.toInt()) {
                1 -> {
                    name.append("n_")
                }
                2 -> {
                    name.append("al_l_")
                }
                3 -> {
                    name.append("al_u_")
                }
            }
            name.append(position)
            name.append(".json")
            val file = File(dir, name.toString())

            try {
                val writer = FileWriter(file, false)
                val arr = JSONArray()
                for (p in timePoints) {
                    val object1 = JSONObject()
                    object1.put("X", p.x.toDouble())
                    object1.put("Y", p.y.toDouble())
                    arr.put(object1)
                }
                writer.append(arr.toString(4))
                writer.append('\n')
                writer.close()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        fun getTextfromExtern(item: HomeItem, position: Int): List<TimePoint> {
            
            val dir = File(Environment.getExternalStorageDirectory(), "KidsApp")
            if (!dir.exists())
                dir.mkdirs()
            val name = StringBuilder()
            when (item.id.toInt()) {
                1 -> {
                    name.append("n_")
                }
                2 -> {
                    name.append("al_l_")
                }
                3 -> {
                    name.append("al_u_")
                }
            }
            name.append(position)
            name.append("_d")
//            name.append(".json")

//            val file = File(dir, name.toString())

            val resources = KidsApp.context?.resources
            val ins = resources?.openRawResource(
                    resources.getIdentifier(name.toString(),
                            "raw",
                            KidsApp.context?.packageName))
            val s = Scanner(ins).useDelimiter("\\A")
            val result = if (s.hasNext()) s.next() else ""

            val list = ArrayList<TimePoint>()
            try {
                val data = result
                val arr = JSONArray(data)
                var i = 0

                while (i < arr.length()) {

                    val obj = arr.optJSONObject(i)
                    val x = obj.optDouble("X").toFloat()
                    val y = obj.optDouble("Y").toFloat()

                    val p = TimePoint.from(x, y)
                    list.add(p)
                    i++
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return list
        }

        fun isPermissionGranted(activity: Activity): Boolean {
            return ActivityCompat.
                    checkSelfPermission(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.
                    checkSelfPermission(activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        fun showPermissionDialog(activity: Activity) {
            if (isPermissionGranted(activity))
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_CONTACTS),
                        100)
        }
    }
}
