package com.kotme

import app.thelema.js.JsApp
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLCanvasElement

fun main() {
    window.onload = {
        val startApp = document.getElementById("startApp") as HTMLButtonElement
        startApp.addEventListener("click", {
            val app = JsApp(document.getElementById("canvas") as HTMLCanvasElement)

            Common.init()

            Movie.init()

            app.startLoop()
        })
    }
}