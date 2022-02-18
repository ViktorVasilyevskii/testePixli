package com.vasilyevskii.test

import android.app.Application
import com.vasilyevskii.test.api.PixliService
import java.io.File
import kotlin.random.Random

class App : Application() {

    val namePutExtraTarget = "target"

    val pixliService = PixliService()


    val filenameSharedPreferences = "StateUser"

    fun generatorId(): Int{
        return Random.nextInt(0, 1000000)
    }

}