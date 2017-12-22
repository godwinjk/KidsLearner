package com.godwin.drawview.support

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.godwin.drawview.KidsApp
import com.godwin.drawview.model.HomeItem
import com.godwin.handdrawview.TimePoint
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException

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
            name.append(".png")
            val file = File(dir, name.toString())
            if (!file.exists())
                return null;
            return BitmapFactory.decodeFile(file.absolutePath)
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
            name.append(".json")
            val file = File(dir, name.toString())
            val list = ArrayList<TimePoint>()
            try {
                val data = file.readText(Charsets.UTF_8)
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
    }
}
