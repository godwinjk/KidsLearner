package com.godwin.drawview.support

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

/**
 * Created by Godwin on 12/24/2017 12:08 AM for DrawView.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class SpeechHelper {
    companion object {
        private val MUTEX = Object()
        private var sInstance: SpeechHelper? = null
        fun getInstance(): SpeechHelper {
            if (null == sInstance) {
                synchronized(MUTEX) {
                    sInstance = SpeechHelper()
                }
            }
            return sInstance!!
        }
    }

    private var tts: TextToSpeech? = null

    private constructor() {
        //restrict the object creation
    }

    public fun speak(context: Context, s: String) {
        if (null == tts) {
            initService(context, s)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts?.speak(s, TextToSpeech.QUEUE_FLUSH, null, "id")
            } else {
                tts?.speak(s, TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    public fun shutDown() {
        if (null != tts)
            tts?.shutdown()
    }

    public fun initService(context: Context, s: String) {
        tts = TextToSpeech(context, TextToSpeech.OnInitListener { a ->
            if (a == TextToSpeech.SUCCESS) {

                val result = tts?.setLanguage(Locale.UK)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported")
                } else {
                    speak(context, s)
                }
            } else {
                Log.e("TTS", "Initilization Failed!")
            }
        })
    }
}