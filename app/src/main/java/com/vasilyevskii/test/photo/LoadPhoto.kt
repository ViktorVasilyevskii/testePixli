package com.vasilyevskii.test.photo

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.vasilyevskii.test.App
import com.vasilyevskii.test.R
import java.io.File

class LoadPhoto: AppCompatActivity(){

    private val nameFile = "filename.jpg"
    private val pathImage = File(filesDir, nameFile)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_photo)
        val imageView = findViewById<ImageView>(R.id.image_view_load_photo)
        Picasso.get()
            .load(pathImage)
            .fit()
            .into(imageView)
    }

    override fun onBackPressed() {
        val stateUser = getSharedPreferences(App().filenameSharedPreferences, Context.MODE_PRIVATE)
        stateUser.edit().putBoolean("returnAlertDialog", true).apply()
        super.onBackPressed()
    }
}