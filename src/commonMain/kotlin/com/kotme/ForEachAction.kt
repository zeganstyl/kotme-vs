package com.kotme

import app.thelema.action.ActionAdapter
import app.thelema.action.ActionData

class ForEachAction: ActionAdapter() {
    override var actionData: ActionData = ActionData()

    override val componentName: String
        get() = "ForEachAction"

    override fun update(delta: Float): Float = 0f
}