package com.kotme

import android.app.Activity
import android.os.Bundle
import app.thelema.android.AndroidApp
import app.thelema.gl.GL

class AndroidMain : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = AndroidApp(this)
        setContentView(app.view)

        GL.call {
            Common.init()
            Movie.init()
        }

        app.startLoop()
    }
}