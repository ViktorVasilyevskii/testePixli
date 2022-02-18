package com.vasilyevskii.test.photo

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.camera.core.*

import androidx.camera.view.PreviewView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.vasilyevskii.test.App
import com.vasilyevskii.test.R
import com.vasilyevskii.test.api.PixliService
import com.vasilyevskii.test.api.model.FIO
import com.vasilyevskii.test.api.model.ImageDTO
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File


class MakePhotoActivity : AppCompatActivity(){

    private val PERMISSION_REQUEST_CAMERA = 83854

    private var imageCapture: ImageCapture? = null

    private val compositeDisposable = CompositeDisposable()

    private val pixliService: PixliService
        get() = (application as App).pixliService

    private val STATE_KEY_NAME = "name"
    private val STATE_KEY_SURNAME = "surname"
    private val STATE_KEY_PATRONYMIC = "patronymic"
    private val RETURN_KEY_ALERT = "returnAlertDialog"

    private val nameFile = "filename.jpg"
    private val pathImage = File(filesDir, nameFile)



    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_photo)

        val stateUser = getSharedPreferences(App().filenameSharedPreferences, Context.MODE_PRIVATE)

        if(stateUser.getBoolean(RETURN_KEY_ALERT, true)){
            alertDialogRequestSendPhoto()
            stateUser.edit().putBoolean("returnAlertDialog", false).apply()
        }else if(pathImage.isFile) {
            val imageViewPhoto = findViewById<ImageView>(R.id.image_activity_make_photo)
            Picasso.get()
                .load(pathImage)
                .into(imageViewPhoto)
        }

        val targetEditText = findViewById<EditText>(R.id.edit_text_target)
        targetEditText.setText(intent.getStringExtra(App().namePutExtraTarget))

        buttonTakePhotoListener()
        buttonTakePhotoCameraX()
    }


    private fun buttonTakePhotoListener(){
        findViewById<Button>(R.id.button_take_photo).setOnClickListener {
                checkPermission()
        }
    }

    private fun buttonTakePhotoCameraX(){
        findViewById<Button>(R.id.button_take_photo_camerax).setOnClickListener {
            takePhoto()
        }
    }

    private fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
                showDialogOK(getString(R.string.text_request_permission_camera)) { _, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            requestPermissions()
                            startCamera()
                        }
                        DialogInterface.BUTTON_NEGATIVE -> finish()
                    }
                }
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
            }
        }else startCamera()
    }

    private fun requestPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CAMERA)
            return false
        }
        return true
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(this.getString(android.R.string.ok), okListener)
            .setNegativeButton(this.getString(android.R.string.cancel), okListener)
            .create()
            .show()
    }


    private fun startCamera(){

        findViewById<LinearLayout>(R.id.liner_layout_info_target).visibility = View.GONE
        findViewById<LinearLayout>(R.id.liner_layout_camera).visibility = View.VISIBLE

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { mPreview ->
                    mPreview.setSurfaceProvider(
                        findViewById<PreviewView>(R.id.preview_camera).surfaceProvider
                    )
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector,
                    preview, imageCapture
                )
            }catch (ex: Exception){
                Log.d("startCamera", "startCamera Fail:", ex)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto(){
        val imageCapture = imageCapture ?: return

        val outputOption = ImageCapture
            .OutputFileOptions
            .Builder(pathImage)
            .build()

        imageCapture.takePicture(
            outputOption, ContextCompat.getMainExecutor(this),
            object :ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    alertDialogRequestSendPhoto()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("takePhoto", "onError: ${exception.message}", exception)
                }

            }
        )

    }

    private lateinit var fio: FIO
    private fun alertDialogRequestSendPhoto(){
        val alertRequestSendPhoto = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.alert_send_photo, null)

        val nameEditText = view.findViewById<EditText>(R.id.edit_alert_send_name)
        val surnameEditText = view.findViewById<EditText>(R.id.edit_alert_send_surname)
        val patronymicEditText = view.findViewById<EditText>(R.id.edit_alert_send_patronymic)

        alertRequestSendPhoto.setView(view)
        alertRequestSendPhoto.setCancelable(false)

        val dialog = alertRequestSendPhoto.create()

        val stateUser = getSharedPreferences(App().filenameSharedPreferences, Context.MODE_PRIVATE)
        if(stateUser.getString(STATE_KEY_NAME, "") != null){
            nameEditText.setText(stateUser.getString(STATE_KEY_NAME, ""))
        }
        if(stateUser.getString(STATE_KEY_SURNAME, "") != null){
            surnameEditText.setText(stateUser.getString(STATE_KEY_SURNAME, ""))
        }
        if(stateUser.getString(STATE_KEY_PATRONYMIC, "") != null){
            patronymicEditText.setText(stateUser.getString(STATE_KEY_PATRONYMIC, ""))
        }

        view.findViewById<TextView>(R.id.text_alert_send_name_photo).apply {
            this.text = nameFile

            setOnClickListener {
                fio = FIO(nameEditText.text.toString(),
                    surnameEditText.text.toString(),
                    patronymicEditText.text.toString())
                saveTextSharedPreferences(fio)

                dialog.dismiss()
                val intent = Intent(view.context, LoadPhoto::class.java)
                view.context.startActivity(intent)
            }
        }

        view.findViewById<Button>(R.id.button_alert_reshoot).setOnClickListener {
            dialog.dismiss()
            onRestart()
            startCamera()
        }

        view.findViewById<Button>(R.id.button_alert_no).setOnClickListener {
            dialog.dismiss()
            onRestart()
        }

        view.findViewById<Button>(R.id.button_alert_yes).setOnClickListener {
            dialog.dismiss()
            if(validationEditText(nameEditText) &&
                        validationEditText(surnameEditText) &&
                        validationEditText(patronymicEditText)){

                val array: Array<String> = arrayOf(nameEditText.text.toString(), surnameEditText.text.toString(), patronymicEditText.text.toString())
                loadImage(App().generatorId(), array, view)

            }else onSnackBar(view, resources.getString(R.string.text_snackbar_edittext_empty))


        }
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.show()


    }

    private val send_data = 18022022
    private fun loadImage(id: Int, array: Array<String>, view: View){
        compositeDisposable.add(pixliService.getPixliApi().uploadImage(send_data,
            id, sendRequestImage(), array)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onSnackBar(view, resources.getString(R.string.text_snackbar_send_data_server))
                findViewById<EditText>(R.id.edit_text_response).setText(it.toString())
                       },{
                           onSnackBar(view, resources.getString(R.string.text_snackbar_no_send_data_server))
            }
            ))
    }

    private fun sendRequestImage(): MultipartBody.Part {
        val bitmap = BitmapFactory.decodeFile(pathImage.absolutePath)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()

        return MultipartBody.Part.createFormData(
            "photo[content]", nameFile,
            byteArray.toRequestBody("/image*".toMediaTypeOrNull(), 0, byteArray.size)
        )
    }

    private fun validationEditText(editText: EditText) : Boolean{
        if(editText.text.isNotEmpty()) return true

        return false
    }

    private fun onSnackBar(view: View, textInfo: String){
        Snackbar.make(view, textInfo, Snackbar.LENGTH_LONG).setAction("Action", null).show()
    }

    override fun onRestart() {
        super.onRestart()
        val intent = intent
        finish()
        startActivity(intent)
    }

    private fun saveTextSharedPreferences(fio: FIO){
        val stateUser = getSharedPreferences(App().filenameSharedPreferences, Context.MODE_PRIVATE)
        stateUser.edit().apply{
            putString(STATE_KEY_NAME, fio.name)
            putString(STATE_KEY_SURNAME, fio.surname)
            putString(STATE_KEY_PATRONYMIC, fio.patronymic)
        }.apply()
    }


}