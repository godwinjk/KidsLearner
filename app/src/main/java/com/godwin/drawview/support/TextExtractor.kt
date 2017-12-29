package com.godwin.drawview.support

import com.godwin.drawview.model.HomeItem

/**
 * Created by Godwin on 12/24/2017 9:50 AM for DrawView.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class TextExtractor {
    companion object {
        fun getText(item: HomeItem, position: Int): String {
            var s = String()
            when (item.id.toInt()) {
                1 -> {
                    s = String.format("%d", position)
                }
                2 -> {
                    var ascInt: Int = position
                    ascInt = ascInt.plus(97)
                    val ascChar: Char = ascInt.toChar()
                    s = ascChar.toString()
                }
                3 -> {
                    var ascInt: Int = position
                    ascInt = ascInt.plus(65)
                    val ascChar: Char = ascInt.toChar()
                    s = ascChar.toString()
                }
            }
            return s;
        }
    }
}