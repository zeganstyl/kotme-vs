package com.kotme

import app.thelema.lwjgl3.JvmApp
import app.thelema.lwjgl3.Lwjgl3WindowConf

fun main() {
    val app = JvmApp(
        Lwjgl3WindowConf {
            msaaSamples = 4
            width = 1280
            height = 720
        }
    )

    visualScriptMain()

    app.startLoop()
}