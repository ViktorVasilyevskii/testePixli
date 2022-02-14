package com.vasilyevskii.test

import android.app.Application
import com.squareup.picasso.Picasso
import com.vasilyevskii.test.api.PixliService

class App : Application() {

    val pixliService = PixliService()
    val picasso = Picasso.get()

}