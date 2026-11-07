package com.zukesito.gamejam

import android.app.Application
import com.zukesito.gamejam.di.AppContainer
import com.zukesito.gamejam.di.DefaultAppContainer

// Creates the single AppContainer instance for the app
class GameJamApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
