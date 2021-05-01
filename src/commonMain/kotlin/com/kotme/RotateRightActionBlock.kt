package com.kotme

import app.thelema.action.IAction
import app.thelema.action.RotateYAction
import app.thelema.math.MATH

class RotateRightActionBlock: ActionBlockAdapter() {
    var rotateAction = RotateYAction().apply { this.angleLength = RIGHT_RAD }

    override val action: IAction
        get() = rotateAction

    init {
        title.text = "Поворот вправо"
        add(title).padRight(10f)
    }
}