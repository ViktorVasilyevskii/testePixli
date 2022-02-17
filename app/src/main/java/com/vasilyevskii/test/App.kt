package com.vasilyevskii.test

import android.app.Application
import com.vasilyevskii.test.api.PixliService
import java.io.File

class App : Application() {

    val namePutExtraTarget = "target"
    val nameFile = "filename.jpg"

    val pixliService = PixliService()

    val pathImage = File(filesDir, nameFile)

}