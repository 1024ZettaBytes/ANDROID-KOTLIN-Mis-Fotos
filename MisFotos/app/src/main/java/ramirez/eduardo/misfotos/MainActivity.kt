package ramirez.eduardo.misfotos

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val PERMISO_CAMARA = 234
    val PERMISO_ALMACENA =666
    val CODIGO_CAMARA = 777
    val CODIGO_ALMACENA = 555
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnTomarFoto.setOnClickListener() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Verifica version
                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    val permiso = arrayOf(android.Manifest.permission.CAMERA)
                    requestPermissions(permiso, PERMISO_CAMARA)
                } else {
                    // Si tengo los permisos
                    tomarFoto()
                }
            } else {
                //Si la version es menor
                tomarFoto()
            }
        }
        btnAgregarFoto.setOnClickListener(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Verifica version
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permiso = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permiso, PERMISO_ALMACENA)
                } else {
                    // Si tengo los permisos
                    subirFoto()
                }
            } else {
                //Si la version es menor
                subirFoto()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISO_CAMARA -> {
                // Si acepto permisos
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tomarFoto()
                } else {
                    //No acepto´los permisos
                    Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISO_ALMACENA->{
                // Si acepto permisos
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    subirFoto()
                } else {
                    //No acepto´los permisos
                    Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun subirFoto() {
        val carpetas_intent = Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(carpetas_intent, CODIGO_ALMACENA)    }

    private fun tomarFoto() {
        val camara_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(camara_intent, CODIGO_CAMARA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            CODIGO_CAMARA->{
                // Compruba que el usuario tomó la foto
                if(resultCode==Activity.RESULT_OK && data!=null){
                    var img = data.extras.get("data") as Bitmap
                    foto.setImageBitmap(img)
                }
            }
            CODIGO_ALMACENA->{
                // Compruba que el usuario seleccionó la imagen
                if(resultCode==Activity.RESULT_OK && data!=null){
                    var img = data.data
                    val ubicacion = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = contentResolver.query(img, ubicacion, null, null,null)
                    cursor.moveToFirst()
                    // Ya estamos en la imagen
                    var identificador = cursor.getColumnIndex(ubicacion[0])
                    var imgPath = cursor.getString(identificador)
                    cursor.close()
                    foto.setImageBitmap(BitmapFactory.decodeFile(imgPath))
                }
            }
        }
    }
}

