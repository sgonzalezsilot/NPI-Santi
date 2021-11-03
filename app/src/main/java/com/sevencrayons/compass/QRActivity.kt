package com.sevencrayons.compass

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.integration.android.IntentIntegrator
import android.content.Intent
import android.os.VibrationEffect
import android.view.View
import android.widget.ImageView
import com.google.zxing.integration.android.IntentResult
import com.squareup.picasso.Picasso
import android.os.Vibrator
import com.sevencrayons.compass.databinding.ActivityQractivityBinding

/**
 * Qr activity
 *
 * @constructor Create empty Q r activity
 */
class QRActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qractivity)
//        IntentIntegrator(this).initiateScan()

        //Boton
        var binding = ActivityQractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.setOnClickListener{initScanner(beep = false)}    
    }

    /**
     * Init scanner
     * Función que inicializa el Scanner para que lea Qr's
     *
     * @param beep Valor booleano para que haga un pequeño pitido el dispositivo móvil
     */
    private fun initScanner(beep: Boolean){

        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        integrator.setBeepEnabled(beep)
        integrator.initiateScan()
    }

    /**
     * Vibrate phone
     *
     * Función para que vibre el móvil levemente
     */
    private fun vibratePhone(){
        val vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(20)
    }

    /**
     * On activity result
     * Esta función se llama para obtener los resultados del Scanner de QR.
     * Ya que en esta app los resultados son la imagen, la guarda y la muestra en un ImageView
     *
     * @param requestCode Código con el que se ha iniciado la actividad
     * @param resultCode Código que devuelve
     * @param data Los datos que devuelve como resultado la actividad
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        // Para tener una pequeña respuesta aptica
        vibratePhone()

        val datos = result.contents


        //Cargo la
        val imageView = findViewById<View?>(R.id.imageView) as ImageView

        //Para mostrar la imagen
        Picasso.with(this)
            .load(datos)
            .resize(100, 100)
//                .centerInside()
            .into(imageView)
    }
}