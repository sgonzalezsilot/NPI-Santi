package com.example.compassdemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.hardware.*
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.location.Location
import android.location.LocationManager
import android.widget.TextView

/**
 * Main activity
 *
 * @constructor Create empty Main activity
 */
//class MainActivity : AppCompatActivity() {
//    private var imageView: ImageView? = null
//    private var botonCompassToScanner: Button? = null
//    private var sensorManager: SensorManager? = null
//    private var sensorAccelerometer: Sensor? = null
//    private var sensorMagneticField: Sensor? = null
//    private var floatGravity = FloatArray(3)
//    private var floatGeoMagnetic = FloatArray(3)
//    private val floatOrientation = FloatArray(3)
//    private val floatRotationMatrix = FloatArray(9)
//
//
//    /**
//     * On create
//     *
//     * @param savedInstanceState
//     */
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//
//        // Botón para abrir scanner
//        var botonCompassToScanner = findViewById(com.example.compassdemo.R.id.CompassToScanner) as Button
//        botonCompassToScanner.setOnClickListener(View.OnClickListener { v: View? ->
//            val intent = Intent(this, QRActivity::class.java)
//            startActivity(intent)
//        })
//
//        // Cogemos la imagen
//        imageView = findViewById(R.id.imageView)
//        // Iniciamos el servicio del sensor
//        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//
//        // Acelerometro y Campo magnético
//        sensorAccelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        sensorMagneticField = sensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
//
//        // Para ir 'escuchando el acelerómetro'
//        val sensorEventListenerAccelrometer: SensorEventListener = object : SensorEventListener {
//
//            /**
//             * On sensor changed (Acelerónmetro)
//             *
//             * @param event
//             */
//            override fun onSensorChanged(event: SensorEvent) {
//                floatGravity = event.values
//                SensorManager.getRotationMatrix(
//                    floatRotationMatrix,
//                    null,
//                    floatGravity,
//                    floatGeoMagnetic
//                )
//                // Se coge la orientación y se actualiza
//                SensorManager.getOrientation(floatRotationMatrix, floatOrientation)
//                imageView?.setRotation((-floatOrientation[0] * 180 / 3.14159).toFloat())
//            }
//
//            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
//        }
//
//
//        val sensorEventListenerMagneticField: SensorEventListener = object : SensorEventListener {
//            /**
//             * On sensor changed (Campo Magnético)
//             *
//             * @param event
//             */
//            override fun onSensorChanged(event: SensorEvent) {
//                floatGeoMagnetic = event.values
//                SensorManager.getRotationMatrix(
//                    floatRotationMatrix,
//                    null,
//                    floatGravity,
//                    floatGeoMagnetic
//                )
//                SensorManager.getOrientation(floatRotationMatrix, floatOrientation)
//                imageView?.setRotation((-floatOrientation[0] * 180 / 3.14159).toFloat())
//            }
//
//            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
//        }
//        sensorManager!!.registerListener(
//            sensorEventListenerAccelrometer,
//            sensorAccelerometer,
//            SensorManager.SENSOR_DELAY_NORMAL
//        )
//        sensorManager!!.registerListener(
//            sensorEventListenerMagneticField,
//            sensorMagneticField,
//            SensorManager.SENSOR_DELAY_NORMAL
//        )
//    }
//
//    /**
//     * Reset button
//     *
//     * @param view
//     */
//    fun ResetButton(view: View?) {
//        imageView!!.rotation = 180f
//    }
//}

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var image: ImageView? = null
    private var currentDegree = 0f
    var mSensorManager: SensorManager? = null
    var mMagneticSensor: Sensor? = null
    var geoField: GeomagneticField? = null
    private var tvHeading: TextView? = null
    private val location: Location = Location("A")
    private val target: Location = Location("B")
    private val locationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        location.setLatitude(37.22858074905951)
        location.setLongitude(-3.6574606928263047)

        tvHeading = findViewById(R.id.tvHeading) as TextView?
        image = findViewById(R.id.imageView) as ImageView?
//        tvHeading = findViewById(R.id.tvHeading) as TextView?
        geoField = GeomagneticField(location.latitude.toFloat(),location.longitude.toFloat(),location.altitude.toFloat(),System.currentTimeMillis())
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mMagneticSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (mMagneticSensor is Sensor){
            // Botón para abrir scanner
            var botonCompassToScanner = findViewById(com.example.compassdemo.R.id.CompassToScanner) as Button
            botonCompassToScanner.setOnClickListener(View.OnClickListener { v: View? ->
                val intent = Intent(this, QRActivity::class.java)
                startActivity(intent)
            })
        }





//        37.22827404128558, -3.65301045280279
        target.setLatitude(37.22827404128558)
        target.setLatitude(-3.65301045280279)
    }

    override fun onResume() {
        super.onResume()
        mSensorManager?.registerListener(
            this, mSensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )
//        mSensorManager?.registerListener(this,mMagneticSensor,SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(this) // to stop the listener and save battery
    }

    override fun onSensorChanged(event: SensorEvent) {
//
//        geoField =
//
//        private final LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                geoField = new GeomagneticField(
//                        Double.valueOf(location.getLatitude()).floatValue(),
//                Double.valueOf(location.getLongitude()).floatValue(),
//                Double.valueOf(location.getAltitude()).floatValue(),
//                System.currentTimeMillis()
//                )
//            }
//        }

        var degree = Math.round(event.values.get(0)).toFloat()
        degree += geoField?.declination!!
        var bearing = location.bearingTo(target)
        degree = bearing - (bearing + degree)
        degree = normalizarGrados(degree)

        tvHeading?.setText("Bearing: " + java.lang.Float.toString(bearing) + " degrees y " + java.lang.Float.toString(degree) + "degrees")
        val ra = RotateAnimation(
            currentDegree,
            -degree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        ra.setDuration(210)
        ra.setFillAfter(true)
        image?.startAnimation(ra)
        currentDegree = -degree
    }

    fun normalizarGrados(value: Float): Float{
        if (value >= 0.0f && value <= 180.0f){
            return value
        }else{
            return 180 + (180 + value)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // not in use
    }
}
