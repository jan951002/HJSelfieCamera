package com.jan.hjselfiecamera.ui.face

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.*
import android.util.Log
import android.util.SparseArray
import android.view.View
import androidx.core.util.forEach
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.LensEngine
import com.huawei.hms.mlsdk.common.MLAnalyzer
import com.huawei.hms.mlsdk.common.MLResultTrailer
import com.huawei.hms.mlsdk.face.MLFace
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting
import com.huawei.hms.mlsdk.face.MLMaxSizeFaceTransactor
import com.jan.hjselfiecamera.R
import com.jan.hjselfiecamera.base.BaseActivity
import com.jan.hjselfiecamera.ui.overlay.LocalFaceGraphic
import kotlinx.android.synthetic.main.activity_live_face_camera.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class LiveFaceCameraActivity : BaseActivity() {

    companion object {
        const val DETECT_MODE_MOST_PEOPLE = 1002
        const val DETECT_MODE_NEAREST_PEOPLE = 1003
        const val DETECT_MODE_INTENT_TAG = "detectMode"
        const val SMILING_POSSIBILITY = 0.95F
        const val SMILING_RATE = 0.8F

        const val STOP_PREVIEW = 1
        const val TAKE_PHOTO = 2
    }

    private lateinit var analyzer: MLFaceAnalyzer
    private lateinit var mLensEngine: LensEngine
    private var lensType = LensEngine.FRONT_LENS
    private var detectMode = 0
    private var safeToTakePicture = false

    override fun layoutRes(): Int = R.layout.activity_live_face_camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            lensType = savedInstanceState.getInt("lensType")
        }
        detectMode = intent.getIntExtra(DETECT_MODE_INTENT_TAG, 1)
        createFaceAnalyzer()
        createLensEngine()
        cameraSwitchButton.setOnClickListener { switchCamera() }
        cameraRestartButton.setOnClickListener { startPreview() }
    }

    override fun onResume() {
        super.onResume()
        startLensEngine()
    }

    override fun onPause() {
        super.onPause()
        lensPreview.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLensEngine.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("lensType", lensType)
        super.onSaveInstanceState(outState)
    }

    private fun createFaceAnalyzer() {
        val setting = MLFaceAnalyzerSetting.Factory()
            .setFeatureType(MLFaceAnalyzerSetting.TYPE_FEATURES)
            .setKeyPointType(MLFaceAnalyzerSetting.TYPE_UNSUPPORT_KEYPOINTS)
            .setMinFaceProportion(0.1F)
            .setTracingAllowed(true)
            .create()
        analyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer(setting)
        if (detectMode == DETECT_MODE_NEAREST_PEOPLE) {
            detectModeNearestPeople()
        } else {
            detectModeMostPeople()
        }
    }

    private fun detectModeNearestPeople() {
        val transactor =
            MLMaxSizeFaceTransactor.Creator(analyzer, object : MLResultTrailer<MLFace?>() {
                override fun objectCreateCallback(itemId: Int, obj: MLFace?) {
                    super.objectCreateCallback(itemId, obj)
                    faceOverlay.clear()
                    if (obj == null) {
                        return
                    }
                    val faceGraphic =
                        LocalFaceGraphic(faceOverlay, obj, this@LiveFaceCameraActivity)
                    faceOverlay.addGraphics(faceGraphic)
                    val emotion = obj.emotions
                    if (emotion.smilingProbability > SMILING_POSSIBILITY) {
                        safeToTakePicture = false
                        mHandler.sendEmptyMessage(TAKE_PHOTO)
                    }
                }

                override fun objectUpdateCallback(
                    var1: MLAnalyzer.Result<MLFace?>?, obj: MLFace?
                ) {
                    faceOverlay.clear()
                    if (obj == null) {
                        return
                    }
                    val faceGraphic =
                        LocalFaceGraphic(faceOverlay, obj, this@LiveFaceCameraActivity)
                    faceOverlay.addGraphics(faceGraphic)
                    val emotion = obj.emotions
                    if (emotion.smilingProbability > SMILING_POSSIBILITY && safeToTakePicture) {
                        safeToTakePicture = false
                        mHandler.sendEmptyMessage(TAKE_PHOTO)
                    }
                }

                override fun lostCallback(result: MLAnalyzer.Result<MLFace?>?) {
                    faceOverlay.clear()
                }

                override fun completeCallback() {
                    faceOverlay.clear()
                }
            }).create()
        analyzer.setTransactor(transactor)
    }

    private fun detectModeMostPeople() {
        analyzer.setTransactor(object : MLAnalyzer.MLTransactor<MLFace> {
            override fun destroy() {
            }

            override fun transactResult(result: MLAnalyzer.Result<MLFace>?) {
                val faceSparseArray = result?.analyseList ?: SparseArray()
                var flag = 0
                faceSparseArray.forEach { _, value ->
                    val emotion = value.emotions
                    if (emotion.smilingProbability > SMILING_POSSIBILITY) {
                        flag++
                    }
                }
                if (flag > faceSparseArray.size() * SMILING_RATE && safeToTakePicture) {
                    safeToTakePicture = false
                    mHandler.sendEmptyMessage(TAKE_PHOTO)
                }
            }
        })
    }

    private fun createLensEngine() {
        val context = this.applicationContext
        mLensEngine = LensEngine.Creator(context, analyzer).setLensType(lensType)
            .applyDisplayDimension(640, 480)
            .applyFps(25.0f)
            .enableAutomaticFocus(true)
            .create()
    }

    private fun startLensEngine() {
        cameraRestartButton.visibility = View.GONE
        try {
            if (detectMode == DETECT_MODE_NEAREST_PEOPLE) {
                lensPreview.start(mLensEngine, faceOverlay)
            } else {
                lensPreview.start(mLensEngine)
            }
            safeToTakePicture = true
        } catch (e: IOException) {
            mLensEngine.release()
        }
    }

    private fun switchCamera() {
        lensType =
            if (lensType == LensEngine.FRONT_LENS) LensEngine.BACK_LENS else LensEngine.FRONT_LENS
        mLensEngine.close()
        startPreview()
    }

    private fun startPreview() {
        lensPreview.release()
        createFaceAnalyzer()
        createLensEngine()
        startLensEngine()
    }

    fun stopPreview() {
        cameraRestartButton.visibility = View.VISIBLE
        mLensEngine.release()
        safeToTakePicture = false
        try {
            analyzer.stop()
        } catch (e: IOException) {
            Log.e(
                LiveFaceCameraActivity::class.java.name,
                "ERROR ON INIT CAMERA ${e.localizedMessage}"
            )
        }
    }

    private fun takePhoto() {
        mLensEngine.photograph(null, { bytes ->
            mHandler.sendEmptyMessage(STOP_PREVIEW)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            saveBitmapToGallery(bitmap)
        })
    }

    private fun saveBitmapToGallery(bitmap: Bitmap): String {
        val appDirectory =
            File(
                "${getExternalFilesDir(null)?.absolutePath}${Environment.DIRECTORY_PICTURES}"
            )
        if (!appDirectory.exists()) {
            val isCreated = appDirectory.mkdir()
            Log.i(LiveFaceCameraActivity::class.java.name, "DIRECTORY CREATED $isCreated")

        }
        val fileName = "${getString(R.string.app_name)}_${System.currentTimeMillis()}.jpg"
        val file = File(appDirectory, fileName)
        if (!file.exists()) file.createNewFile()
        try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            MediaScannerConnection.scanFile(this, arrayOf(file.toString()), null, null)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return file.absolutePath
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                STOP_PREVIEW -> stopPreview()
                TAKE_PHOTO -> takePhoto()
            }
        }
    }
}