package com.kotme

import app.thelema.js.JsApp
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement

fun main() {
    window.onload = {
        val app = JsApp(document.getElementById("canvas") as HTMLCanvasElement)

        visualScriptMain()

        app.startLoop()
    }
}