package com.kotme

import app.thelema.action.Action
import app.thelema.ui.Window

class ActionBlock(val action: Action): Window(addCloseButton = false) {
    init {
        titleLabel.textProvider = { action.proxy?.componentTypeName ?: "" }
    }
}