package com.pokemon.mebius.commlib.demo

import android.app.Application
import com.pokemon.mebius.commlib.utils.APPLICATION

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        APPLICATION = this
    }
}