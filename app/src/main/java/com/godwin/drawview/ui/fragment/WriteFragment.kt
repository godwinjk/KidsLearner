package com.godwin.drawview.ui.fragment


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import com.godwin.drawview.R
import com.godwin.drawview.model.HomeItem
import com.godwin.drawview.support.FileManager
import com.godwin.drawview.ui.common.AudioManager
import com.godwin.drawview.ui.common.ShareHandler
import com.godwin.drawview.ui.view.ColorPickerDialog
import com.godwin.drawview.ui.view.ColorPickerListener
import com.godwin.drawview.ui.view.WidthSelectDialog
import com.godwin.handdrawview.HandDrawView
import com.godwin.handdrawview.Mode


/**
 * A simple [Fragment] subclass.
 */
class WriteFragment : BaseFragment(), View.OnClickListener {


    private var mHomeItem: HomeItem? = null
    private lateinit var ivReset: ImageView
    private lateinit var ivWrite: ImageView
    private lateinit var ivColor: ImageView
    private lateinit var ivMusic: ImageView

    private lateinit var ivPrev: ImageView
    private lateinit var ivTest: ImageView
    private lateinit var ivNext: ImageView

    private lateinit var ivBack: ImageView
    private lateinit var ivShare: ImageView

    private lateinit var mHandDraw: HandDrawView

    private var position: Int = 0
    private var devMode: Boolean = false

    companion object {
        val ARG_1: String = "arg_1"
        fun newInstance(homeItem: HomeItem): WriteFragment {
            var bundle = Bundle()
            bundle.putParcelable(ARG_1, homeItem)
            val fragment = WriteFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (null != bundle) {
            mHomeItem = bundle.getParcelable(ARG_1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_write, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

        registerEvents()
        fetchImage()
    }

    private fun fetchImage() {
        mHandDraw.overlayBitmap = FileManager.getBitmapFromExtern(mHomeItem!!, position)
    }

    private fun initViews(view: View) {
        mHandDraw = view.findViewById(R.id.hand_draw)
        mHandDraw.strokeWidth = 10
        mHandDraw.strokeColor = Color.RED
        mHandDraw.mode = Mode.MARKER
        mHandDraw.pointerRes = R.drawable.pencil
        mHandDraw.isFadeEffect = true

        ivReset = view.findViewById(R.id.iv_reset)
        ivWrite = view.findViewById(R.id.iv_write)
        ivColor = view.findViewById(R.id.iv_color)
        ivMusic = view.findViewById(R.id.iv_music)

        ivPrev = view.findViewById(R.id.iv_prev)
        ivTest = view.findViewById(R.id.iv_test)
        ivNext = view.findViewById(R.id.iv_next)

        ivBack = view.findViewById(R.id.iv_back)
        ivShare = view.findViewById(R.id.iv_share)

        ivWrite.tag = 1
    }

    private fun registerEvents() {
        ivReset.setOnClickListener(this)
        ivWrite.setOnClickListener(this)
        ivColor.setOnClickListener(this)
        ivMusic.setOnClickListener(this)

        ivPrev.setOnClickListener(this)
        ivTest.setOnClickListener(this)
        ivNext.setOnClickListener(this)

        ivBack.setOnClickListener(this)
        ivShare.setOnClickListener(this)

        ivTest.setOnLongClickListener {
            devMode = devMode.not()
            Toast.makeText(activity!!, "Dev mode" + devMode, Toast.LENGTH_SHORT).show()
            true
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View) {
        AudioManager.playButtonClick()
        when (v.id) {
            R.id.iv_reset -> {
                mHandDraw.clear()
            }
            R.id.iv_write -> {
                ivWrite.tag = 1
                ivWrite.setImageResource(R.drawable.pen)
                mHandDraw.mode = Mode.MARKER

                showStrokeWidthDialog()
            }
            R.id.iv_color -> {
                showColorPicker()
            }
            R.id.iv_music -> {
//                readFromExtern()
//                mHandDraw.startAnimation()
            }
            R.id.iv_prev -> {
                if (position > 0) {
                    position--
                    mHandDraw.clear()
                    fetchImage()
                } else {
                    position = getMaxPosition(mHomeItem!!)
                }
            }
            R.id.iv_test -> {
//                extractPoints()
            }
            R.id.iv_next -> {
                if (!isLastPosition(mHomeItem!!, position)) {
                    position++
                    mHandDraw.clear()
                    fetchImage()
                } else {
                    position = 0
                }
            }
            R.id.iv_back -> {
                mActivity.onBackPressed()
            }
            R.id.iv_share -> {
                ShareHandler.shareApplication(activity)
            }
        }
    }

    private fun showColorPicker() {
        val listener = ColorPickerListener { color -> mHandDraw.strokeColor = color }
        val dialog = ColorPickerDialog(activity, listener)
        dialog.setColor(mHandDraw.strokeColor)
        dialog.show()
    }

    private fun showStrokeWidthDialog() {
        val listener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                mHandDraw.strokeWidth = p1.plus(5)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        }
        val dialog = WidthSelectDialog(activity, mHandDraw.strokeWidth, 5, listener)
        dialog.show()
    }

    private fun readFromExtern() {
        mHandDraw.normalizedPathPoints = FileManager.getTextfromExtern(mHomeItem!!, position)
    }

    private fun extractPoints() {
        FileManager.writeTextToExtern(mHomeItem!!, position, mHandDraw.normalizedPathPoints)
    }
}
