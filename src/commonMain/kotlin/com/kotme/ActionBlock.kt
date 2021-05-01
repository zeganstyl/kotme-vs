package com.kotme

import app.thelema.action.Action
import app.thelema.ui.Window

class ActionBlock(val action: Action): Window(addCloseButton = false) {
    init {
        titleLabel.style = SKIN.label
        titleLabel.textProvider = { action.proxy?.componentName ?: "" }
    }
}