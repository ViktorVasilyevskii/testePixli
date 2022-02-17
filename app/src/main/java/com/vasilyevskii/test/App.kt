package com.vasilyevskii.test

import android.app.Application
import com.vasilyevskii.test.api.PixliService

class App : Application() {

    val namePutExtraTarget = "target"

    val pixliService = PixliService()

}