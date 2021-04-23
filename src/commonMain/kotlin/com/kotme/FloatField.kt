/*
 * Copyright 2020 Anton Trushkov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotme

import app.thelema.g2d.Batch
import app.thelema.input.KB
import app.thelema.studio.widget.PropertyProvider
import app.thelema.ui.InputEvent
import app.thelema.ui.InputListener
import app.thelema.ui.TextField

class FloatField: TextField(style = SKIN.textField), PropertyProvider<Float> {
    var value: Float = 0f
        set(value) {
            if (field != value) {
                field = value
                text = value.toString()
            }
        }

    override var set: (value: Float) -> Unit = {}
    override var get: () -> Float = { 0f }

    override val minWidth: Float
        get() = 50f

    override val prefWidth: Float
        get() = 50f

    init {
        text = value.toString()

        addListener(object : InputListener {
            override fun keyDown(event: InputEvent, keycode: Int): Boolean {
                when (keycode) {
                    KB.ENTER -> {
                        val v = text.toFloatOrNull()
                        if (v != null) set(v)
                    }
                    KB.ESCAPE -> {
                        text = value.toString()
                    }
                    KB.Z -> {
                        if (KB.ctrl) text = value.toString()
                    }
                }
                return super.keyDown(event, keycode)
            }
        })
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (!focused) {
            value = get()
        }
        super.draw(batch, parentAlpha)
    }
}