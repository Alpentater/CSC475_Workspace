package com.example.cta_7

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
//Hilt needs an Application class with '@HiltAndroidApp' so that it becomes the root...
//...of the applciations dependancy graph
@HiltAndroidApp
class UnitConverterApplication : Application()