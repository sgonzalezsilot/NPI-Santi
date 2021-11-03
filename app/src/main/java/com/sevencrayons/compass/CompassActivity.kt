package com.sevencrayons.compass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.sevencrayons.compass.Compass
import android.widget.TextView
import com.sevencrayons.compass.SOTWFormatter
import android.os.Bundle
import android.util.Log
import android.view.View
import com.sevencrayons.compass.R
import com.sevencrayons.compass.CompassActivity
import com.sevencrayons.compass.Compass.CompassListener
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView

class CompassActivity : AppCompatActivity() {
    private var compass: Compass? = null
    private var arrowView: ImageView? = null
    private var sotwLabel // SOTW is for "side of the world"
            : TextView? = null
    private var currentAzimuth = 0f
    private var sotwFormatter: SOTWFormatter? = null
    private var botonCompassToScanner: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)
        // Botón para abrir scanner
        var botonCompassToScanner = findViewById(com.sevencrayons.compass.R.id.CompassToScanner) as Button
        botonCompassToScanner.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this, QRActivity::class.java)
            startActivity(intent)
        })
        sotwFormatter = SOTWFormatter(this)
        arrowView = findViewById(R.id.main_image_hands)
        sotwLabel = findViewById(R.id.sotw_label)
        setupCompass()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "start compass")
        compass!!.start()
    }

    override fun onPause() {
        super.onPause()
        compass!!.stop()
    }

    override fun onResume() {
        super.onResume()
        compass!!.start()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "stop compass")
        compass!!.stop()
    }

    private fun setupCompass() {
        compass = Compass(this)
        val cl = compassListener
        compass!!.setListener(cl)
    }

    fun adjustArrow(azimuth: Float) {
        Log.d(
            TAG, "will set rotation from " + currentAzimuth + " to "
                    + azimuth
        )
        val an: Animation = RotateAnimation(
            -currentAzimuth, -azimuth,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        currentAzimuth = azimuth
        an.duration = 500
        an.repeatCount = 0
        an.fillAfter = true
        arrowView!!.startAnimation(an)
    }

    private fun adjustSotwLabel(azimuth: Float) {
        sotwLabel!!.text = sotwFormatter!!.format(azimuth)
    }

    // UI updates only in UI thread
    // https://stackoverflow.com/q/11140285/444966
    private val compassListener: CompassListener
        private get() = object : CompassListener {
            override fun onNewAzimuth(azimuth: Float) {
                // UI updates only in UI thread
                // https://stackoverflow.com/q/11140285/444966
                runOnUiThread {
                    adjustArrow(azimuth)
                    adjustSotwLabel(azimuth)
                }
            }
        }

    companion object {
        private const val TAG = "CompassActivity"
    }
}