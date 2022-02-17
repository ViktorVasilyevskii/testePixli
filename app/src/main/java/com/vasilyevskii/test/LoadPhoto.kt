package com.vasilyevskii.test

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class LoadPhoto: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_photo)
        val imageView = findViewById<ImageView>(R.id.image_view_load_photo)
        Picasso.get()
            .load(App().pathImage)
            .fit()
            .into(imageView)
    }
}