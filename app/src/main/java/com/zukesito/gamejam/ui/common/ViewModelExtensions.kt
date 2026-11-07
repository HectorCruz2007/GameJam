package com.zukesito.gamejam.ui.common

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.zukesito.gamejam.GameJamApplication
import com.zukesito.gamejam.di.AppContainer

// Gives ViewModel factories access to the single AppContainer
fun CreationExtras.appContainer(): AppContainer =
    (this[APPLICATION_KEY] as GameJamApplication).container
