package com.sevencrayons.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.widget.TextView
import com.sevencrayons.compass.Compass.CompassListener
import android.hardware.SensorManager
import android.hardware.SensorEvent
import android.util.Log
import android.widget.ImageView
import com.sevencrayons.compass.Compass

class Compass(context: Context) : SensorEventListener {
    private val arrowView: ImageView? = null
    private val sotwLabel // SOTW is for "side of the world"
            : TextView? = null
    private val currentAzimuth = 0f

    interface CompassListener {
        fun onNewAzimuth(azimuth: Float)
    }

    private var listener: CompassListener? = null
    private val sensorManager: SensorManager
    private val gsensor: Sensor
    private val msensor: Sensor
    private val mGravity = FloatArray(3)
    private val mGeomagnetic = FloatArray(3)
    private val R = FloatArray(9)
    private val I = FloatArray(9)
    private var azimuth = 0f
    private var azimuthFix = 0f
    fun start() {
        sensorManager.registerListener(
            this, gsensor,
            SensorManager.SENSOR_DELAY_GAME
        )
        sensorManager.registerListener(
            this, msensor,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    fun setAzimuthFix(fix: Float) {
        azimuthFix = fix
    }

    fun resetAzimuthFix() {
        setAzimuthFix(0f)
    }

    fun setListener(l: CompassListener?) {
        listener = l
    }

    //    @Override
    //    public void onSensorChanged(SensorEvent event) {
    //        final float alpha = 0.97f;
    //
    //        synchronized (this) {
    //            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
    //
    //                mGravity[0] = alpha * mGravity[0] + (1 - alpha)
    //                        * event.values[0];
    //                mGravity[1] = alpha * mGravity[1] + (1 - alpha)
    //                        * event.values[1];
    //                mGravity[2] = alpha * mGravity[2] + (1 - alpha)
    //                        * event.values[2];
    //
    //                // mGravity = event.values;
    //
    //                // Log.e(TAG, Float.toString(mGravity[0]));
    //            }
    //
    //            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
    //                // mGeomagnetic = event.values;
    //
    //                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
    //                        * event.values[0];
    //                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
    //                        * event.values[1];
    //                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
    //                        * event.values[2];
    //                // Log.e(TAG, Float.toString(event.values[0]));
    //
    //            }
    //
    //            boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
    //                    mGeomagnetic);
    //            if (success) {
    //                float orientation[] = new float[3];
    //                SensorManager.getOrientation(R, orientation);
    //                // Log.d(TAG, "azimuth (rad): " + azimuth);
    //                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
    //                azimuth = (azimuth + azimuthFix + 360) % 360;
    //                // Log.d(TAG, "azimuth (deg): " + azimuth);
    //                if (listener != null) {
    //                    listener.onNewAzimuth(azimuth);
    //                }
    //            }
    //        }
    //    }
    protected fun bearing(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): Double {
        val latitude1 = Math.toRadians(startLat)
        val latitude2 = Math.toRadians(endLat)
        val longDiff = Math.toRadians(endLng - startLng)
        val y = Math.sin(longDiff) * Math.cos(latitude2)
        val x =
            Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(
                longDiff
            )
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360
    }

    override fun onSensorChanged(event: SensorEvent) {
        val alpha = 0.97f
        synchronized(this) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0]
                mGravity[1] = alpha * mGravity[1] + (1 - alpha)* event.values[1]
                mGravity[2] = alpha * mGravity[2] + (1 - alpha)* event.values[2]

                // mGravity = event.values;

                // Log.e(TAG, Float.toString(mGravity[0]));
            }
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                // mGeomagnetic = event.values;
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)* event.values[0]
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)* event.values[1]
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)* event.values[2]
                // Log.e(TAG, Float.toString(event.values[0]));
            }
            val R = FloatArray(9)
            val I = FloatArray(9)
            val success = SensorManager.getRotationMatrix(
                R, I, mGravity,
                mGeomagnetic
            )
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                // Log.d(TAG, "azimuth (rad): " + azimuth);
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat() // orientation
                azimuth = (azimuth + 360) % 360

                //Casa
//                37.22857891755769, -3.657465067196061
                val yourlatitude = 37.22857891755769
                val yourlongitude = -3.657465067196061

                //Mercadona
//                37.22977727052026, -3.654057962910184
                //Ayuntamiento
//                37.23064905496919, -3.6571940197111896
                //Paseo de Ronda 28
//                37.22431172898537, -3.6559254468408215
                //Iliberis
//                37.22379569436706, -3.681126588858304
                //Etsiit
//                37.19699819151032, -3.624996828763554
                val latWhereToPoint = 37.19699819151032
                val lngWhereToPoint = -3.624996828763554

                azimuth -= bearing(
                    yourlatitude,
                    yourlongitude,
                    latWhereToPoint,
                    lngWhereToPoint
                ).toFloat()
                Log.d(TAG, "azimuth (deg): $azimuth")
                if (listener != null) {
                    listener!!.onNewAzimuth(azimuth)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    companion object {
        private const val TAG = "Compass"
    }

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }
}